@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend

import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.func.makeClient
import dev.triumphteam.backend.location.Api
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.ForwardedHeaderSupport
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.util.KtorExperimentalAPI
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path

fun Application.module() {
    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) { json() }
    install(Github) {
        client = makeClient()
    }

    val repo = Path("data", "repo").toFile()
    repo.walkTopDown().forEach {
        if (it.extension != "md") return@forEach
        val parser = Parser.builder().build()
        val document = parser.parse(it.readText())
        val renderer = HtmlRenderer.builder().build()
        println(renderer.render(document))
    }

    routing {
        get<Api.Test> {
            println("Hello")
            call.respondText("Test")
        }

    }
}