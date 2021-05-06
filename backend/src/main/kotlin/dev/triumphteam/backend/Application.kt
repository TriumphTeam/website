package dev.triumphteam.backend

import io.ktor.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOGGER: Logger = LoggerFactory.getLogger("dev.triumphteam.backend")

fun main() {
    embeddedServer(CIO, module = Application::module, port = 8000).start(true)
}