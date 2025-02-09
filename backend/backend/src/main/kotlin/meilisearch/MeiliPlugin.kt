package dev.triumphteam.backend.meilisearch

import io.ktor.http.URLProtocol
import io.ktor.server.application.Application
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.util.AttributeKey

public class Meili(config: Configuration) {

    public val client: MeiliClient = config.createClient()

    public class Configuration(private val developMode: Boolean) {
        public var host: String = "0.0.0.0"
        public var port: Int = 7700
        public var apiKey: String = "masterKey"
        public var protocol: URLProtocol = URLProtocol.HTTP

        internal fun createClient() = MeiliClient(host, port, apiKey, protocol, developMode)
    }

    public companion object Plugin : BaseApplicationPlugin<Application, Configuration, Meili> {

        override val key: AttributeKey<Meili> = AttributeKey("Meili")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Meili {
            return Meili(Configuration(pipeline.developmentMode).apply(configure))
        }
    }
}
