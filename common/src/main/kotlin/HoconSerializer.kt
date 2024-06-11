package dev.triumphteam.website

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import java.io.File

public object HoconSerializer {

    public val hocon: Hocon = Hocon {}

    public inline fun <reified T> from(file: File): T {
        return from<T>(file.readText())
    }

    public inline fun <reified T> from(string: String): T {
        return hocon.decodeFromConfig<T>(ConfigFactory.parseString(string))
    }
}
