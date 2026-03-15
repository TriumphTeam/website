package dev.triumphteam.frontend.api.plugin

import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.cache.HttpCache.Companion.HttpResponseFromCache
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.content.OutgoingContent
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes
import io.ktor.util.collections.ConcurrentMap
import io.ktor.util.date.GMTDate
import io.ktor.util.pipeline.PipelinePhase
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(InternalAPI::class)
public class ApiCache(config: Config) {

    public companion object : HttpClientPlugin<Config, ApiCache> {
        override val key: AttributeKey<ApiCache> = AttributeKey("ResponseCache")

        override fun prepare(block: Config.() -> Unit): ApiCache {
            return ApiCache(Config().apply(block))
        }

        override fun install(plugin: ApiCache, scope: HttpClient) {
            val cacheRequestPhase = PipelinePhase("Cache")
            scope.sendPipeline.insertPhaseAfter(HttpSendPipeline.State, cacheRequestPhase)

            scope.sendPipeline.intercept(cacheRequestPhase) { content ->
                if (content !is OutgoingContent.NoContent) return@intercept
                if (context.method != HttpMethod.Get) return@intercept

                val cachedCall = plugin.getCached(context, scope) ?: return@intercept

                finish()
                scope.monitor.raise(HttpResponseFromCache, cachedCall.response)
                proceedWith(cachedCall)
            }

            val cacheResponsePhase = PipelinePhase("Cache")
            scope.receivePipeline.insertPhaseAfter(HttpReceivePipeline.State, cacheResponsePhase)

            scope.receivePipeline.intercept(cacheResponsePhase) { response ->
                if (response.call.request.method != HttpMethod.Get) return@intercept
                if (!response.status.isSuccess()) return@intercept
                plugin.cacheResponse(response)
            }
        }
    }

    private val cache = config.cache
    private val expire = config.expire

    public fun getCached(context: HttpRequestBuilder, scope: HttpClient): HttpClientCall? {
        val url = Url(context.url)
        val cached = cache[url] ?: return null

        val now = GMTDate()

        val timeDifference = (now.timestamp - cached.responseTime.timestamp).milliseconds
        if (timeDifference > expire) {
            cache.remove(url) // Clear the value from the cache if expired.
            return null
        }

        return cached.createResponse(scope, RequestForCache(context.build()), context.executionContext).call
    }

    public suspend fun cacheResponse(response: HttpResponse) {
        cache[response.call.request.url] = CachedResponseData(
            url = response.call.request.url,
            statusCode = response.status,
            requestTime = response.requestTime,
            headers = response.headers,
            version = response.version,
            body = response.rawContent.readRemaining().readByteArray(),
            responseTime = response.responseTime,
        )
    }

    public class Config {
        public var cache: MutableMap<Url, CachedResponseData> = ConcurrentMap()
        public var expire: Duration = 15.seconds
    }
}

private class RequestForCache(data: HttpRequestData) : HttpRequest {
    override val call: HttpClientCall
        get() = throw IllegalStateException("This request has no call")
    override val method: HttpMethod = data.method
    override val url: Url = data.url
    override val attributes: Attributes = data.attributes
    override val content: OutgoingContent = data.body
    override val headers: Headers = data.headers
}

public class CachedResponseData(
    public val url: Url,
    public val statusCode: HttpStatusCode,
    public val requestTime: GMTDate,
    public val responseTime: GMTDate,
    public val version: HttpProtocolVersion,
    public val headers: Headers,
    public val body: ByteArray,
) {

    public fun createResponse(
        client: HttpClient,
        request: HttpRequest,
        responseContext: CoroutineContext,
    ): HttpResponse {
        val response = object : HttpResponse() {
            override val call: HttpClientCall get() = throw IllegalStateException("This is a fake response")
            override val status: HttpStatusCode = statusCode
            override val version: HttpProtocolVersion = this@CachedResponseData.version
            override val requestTime: GMTDate = this@CachedResponseData.requestTime
            override val responseTime: GMTDate = this@CachedResponseData.responseTime

            @InternalAPI
            override val rawContent: ByteReadChannel get() = throw IllegalStateException("This is a fake response")
            override val headers: Headers = this@CachedResponseData.headers
            override val coroutineContext: CoroutineContext = responseContext
        }
        return SavedHttpCall(client, request, response, body).response
    }
}

private class SavedHttpCall(
    client: HttpClient,
    request: HttpRequest,
    response: HttpResponse,
    responseBody: ByteArray,
) : HttpClientCall(client) {

    init {
        this.request = SavedHttpRequest(this, request)
        this.response = SavedHttpResponse(this, responseBody, response)
    }

    override val allowDoubleReceive: Boolean = true
}

private class SavedHttpRequest(
    override val call: SavedHttpCall,
    origin: HttpRequest,
) : HttpRequest by origin

private class SavedHttpResponse(
    override val call: SavedHttpCall,
    private val body: ByteArray,
    origin: HttpResponse,
) : HttpResponse() {
    override val status: HttpStatusCode = origin.status

    override val version: HttpProtocolVersion = origin.version

    override val requestTime: GMTDate = origin.requestTime

    override val responseTime: GMTDate = origin.responseTime

    override val headers: Headers = origin.headers

    override val coroutineContext: CoroutineContext = origin.coroutineContext

    @OptIn(InternalAPI::class)
    override val rawContent: ByteReadChannel get() = ByteReadChannel(body)
}
