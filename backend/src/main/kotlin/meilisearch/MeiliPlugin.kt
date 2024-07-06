package dev.triumphteam.backend.meilisearch

import io.ktor.http.URLProtocol
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.application
import io.ktor.server.application.plugin
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext

public class Meili(config: Configuration) {

    public val client: MeiliClient = config.createClient()

    public class Configuration(private val developMode: Boolean) {
        public var host: String = "0.0.0.0"
        public var port: Int = 7700
        public var apiKey: String = "masterKey"
        public var protocol: URLProtocol = URLProtocol.HTTP

        internal fun createClient() = MeiliClient(host, port, apiKey, protocol, developMode)
    }

    public companion object Plugin : BaseApplicationPlugin<Application, Configuration, Meili> {

        override val key: AttributeKey<Meili> = AttributeKey("Meili")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Meili {
            return Meili(Configuration(pipeline.developmentMode).apply(configure))
        }
    }
}

public suspend inline fun <reified T> PipelineContext<*, ApplicationCall>.search(
    index: String,
    query: String,
    limit: Int = 20,
    filter: String? = null,
): List<T> = with(this.application.plugin(Meili).client) {
    return index(index).search(query, limit, filter)
}

public suspend inline fun PipelineContext<*, ApplicationCall>.index(index: String): MeiliClient.Index =
    application.plugin(Meili).client.index(index)
