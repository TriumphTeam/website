package dev.triumphteam.backend

import io.ktor.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

fun main() {
    embeddedServer(CIO, module = Application::module, port = 8000).start(true)
}