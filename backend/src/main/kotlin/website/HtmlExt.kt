package dev.triumphteam.backend.website

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.html.HTML
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private val pageCache: Cache<String, TextContent> = Caffeine.newBuilder()
    .expireAfterWrite(10.minutes.toJavaDuration())
    .build()

public suspend fun ApplicationCall.respondHtmlCached(id: String, block: HTML.() -> Unit) {

    val cached = pageCache.getIfPresent(id)
    if (cached != null) {
        respond(cached)
        return
    }

    val text = buildString {
        append("<!DOCTYPE html>\n")
        appendHTML().html(block = block)
    }

    respond(
        message = TextContent(text, ContentType.Text.Html.withCharset(Charsets.UTF_8), HttpStatusCode.OK).also {
            pageCache.put(id, it)
        }
    )
}
