package dev.triumphteam.website

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

public object JsonSerializer {

    public val json: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    public inline fun <reified T> from(file: File): T {
        return from<T>(file.readText())
    }

    public inline fun <reified T> from(string: String): T {
        return json.decodeFromString<T>(string)
    }

    public inline fun <reified T> encode(serializable: T): String {
        return json.encodeToString<T>(serializable)
    }
}
