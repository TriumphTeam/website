@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend

import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.events.GithubPush
import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.feature.Placeholders
import dev.triumphteam.backend.feature.Project
import dev.triumphteam.backend.feature.listening
import dev.triumphteam.backend.func.JSON
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.makeClient
import dev.triumphteam.backend.routing.pageContentRoute
import dev.triumphteam.backend.routing.pageRoute
import dev.triumphteam.backend.routing.projectsRoute
import dev.triumphteam.backend.routing.summaryRoute
import dev.triumphteam.backend.scheduler.Scheduler
import dev.triumphteam.backend.scheduler.runTaskEvery
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.ForwardedHeaderSupport
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.serialization.json
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.io.path.ExperimentalPathApi
import kotlin.time.Duration.Companion.minutes

/**
 * Module of the application
 */
fun Application.module() {
    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }

    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) { json(JSON) }

    // Custom
    val github = install(Github) { client = makeClient() }

    install(Project)
    install(Scheduler)

    val placeholders = install(Placeholders)

    // Updates the version of the project every 10m
    runTaskEvery(10.minutes) {
        log { "Updating projects version!" }
        transaction {
            Projects.selectAll().forEach { row ->
                val version = runBlocking { github.getRelease(row[Projects.github]) }?.version ?: return@forEach
                if (version == row[Projects.version]) return@forEach

                Projects.update({ Projects.id eq row[Projects.id] }) {
                    it[Projects.version] = version
                }
            }
        }
    }

    listening {
        on<GithubPush> {
            log { "Detected Github push." }

            if (CONFIG[Settings.REPO].name != project) {
                return@on
            }

            github.checkRepository()
        }
    }

    routing {
        summaryRoute()
        pageRoute(placeholders)
        pageContentRoute()
        projectsRoute()
    }

}
