package dev.triumphteam.backend

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

public fun main() {
    embeddedServer(Netty, module = Application::module, port = 8001, watchPaths = listOf("classes")).start(true)
}
