package dev.triumphteam.backend.func

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json

/**
 * Creates a CIO client
 */
fun makeClient() = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}

private const val GITHUB_API = "https://api.github.com/"

fun commits(repo: String) = "${GITHUB_API}repos/$repo/commits"