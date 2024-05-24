package dev.triumphteam.backend

import dev.triumphteam.backend.events.GithubPush
import dev.triumphteam.backend.feature.Project
import dev.triumphteam.backend.feature.listening
import dev.triumphteam.backend.func.JSON
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.routing.pageContentRoute
import dev.triumphteam.backend.routing.projectsRoute
import dev.triumphteam.backend.routing.summaryRoute
import dev.triumphteam.backend.scheduler.Scheduler
import dev.triumphteam.backend.scheduler.runTaskEvery
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.forwardedheaders.ForwardedHeaders
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import kotlin.time.Duration.Companion.minutes

/**
 * Module of the application
 */
fun Application.module() {

    val githubRepoLink = System.getenv("BACKEND_GITHUB_REPOSITORY")
        ?: throw IllegalArgumentException("Application cannot run without the environment variable 'BACKEND_GITHUB_REPOSITORY'.")

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }

    install(Resources)
    install(ForwardedHeaders)
    install(ContentNegotiation) { json(JSON) }

    // Custom

    // val github = install(Github) { client = makeClient() }

    install(Project)
    install(Scheduler)

    // Updates the version of the project every 10m
    runTaskEvery(10.minutes) {
        log { "Updating projects version!" }
        /*transaction {
            Projects.selectAll().forEach { row ->
                val version = runBlocking { github.getRelease(row[Projects.github]) }?.version ?: return@forEach
                if (version == row[Projects.version]) return@forEach

                Projects.update({ Projects.id eq row[Projects.id] }) {
                    it[Projects.version] = version
                }
            }
        }*/
    }

    listening {
        on<GithubPush> {
            log { "Detected Github push." }

            /*if (CONFIG[Settings.REPO].name != project) {
                return@on
            }*/

            github.checkRepository()
        }
    }

    routing {
        summaryRoute()
        // pageRoute(placeholders)
        pageContentRoute()
        projectsRoute()
    }
}
