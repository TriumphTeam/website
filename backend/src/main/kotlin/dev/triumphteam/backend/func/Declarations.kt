package dev.triumphteam.backend.func

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json

/**
 * Creates a CIO client
 */
internal fun makeClient() = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }
        )
    }
}
