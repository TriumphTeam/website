package dev.triumphteam.frontend.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json

public const val API_BASE_URL: String = "http://localhost:8001"

public val api: HttpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }
    defaultRequest {
        url(urlString = API_BASE_URL)
    }
}
