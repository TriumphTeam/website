package dev.triumphteam.backend

import dev.triumphteam.backend.func.log
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// Application's logger
val LOGGER: Logger = LoggerFactory.getLogger("backend")

fun main() {
    embeddedServer(CIO, module = Application::module, port = 8000).start(true)
    log { "Server started" }
}
