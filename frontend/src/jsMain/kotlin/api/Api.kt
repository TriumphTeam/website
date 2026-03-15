package dev.triumphteam.frontend.api

import dev.triumphteam.frontend.api.plugin.ApiCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.minutes

public const val API_BASE_URL: String = "http://192.168.1.65:8001"

public val api: HttpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }

    install(ApiCache) {
        expire = 5.minutes
    }

    defaultRequest {
        url(urlString = API_BASE_URL)
    }
}
