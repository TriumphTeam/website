package dev.triumphteam.backend.website.pages.docs

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.triumphteam.backend.api.database.DocVersionEntity
import dev.triumphteam.backend.api.database.DocVersions
import dev.triumphteam.backend.api.database.PageEntity
import dev.triumphteam.backend.api.database.Pages
import dev.triumphteam.backend.api.database.ProjectEntity
import dev.triumphteam.backend.website.pages.docs.components.dropDown
import dev.triumphteam.backend.website.pages.docs.components.search
import dev.triumphteam.backend.website.pages.docs.components.toast
import dev.triumphteam.backend.website.pages.setupHead
import dev.triumphteam.website.project.Navigation
import dev.triumphteam.website.project.PageSummary
import dev.triumphteam.website.project.SummaryEntry
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private val projectCache: Cache<String, ProjectData> = Caffeine.newBuilder()
    .expireAfterWrite(5.minutes.toJavaDuration())
    .build()

public fun Routing.docsRoutes() {

    get("/docs/{param...}") {

        val (paramVersion, paramProject, paramPage) = call.parameters.extractDocsPath() ?: return@get call.respond(
            HttpStatusCode.NotFound
        )

        val project = getProject(paramProject) ?: return@get call.respond(HttpStatusCode.NotFound)

        val currentVersion = when {
            paramVersion != null -> project.versions[paramVersion] ?: return@get call.respond(HttpStatusCode.NotFound)
            else -> project.versions.values.firstOrNull() ?: return@get call.respond(HttpStatusCode.NotFound)
        }

        val pages = currentVersion.pages
        val page = when {
            paramPage != null -> pages[paramPage] ?: return@get call.respond(HttpStatusCode.NotFound)
            else -> {
                // If the page doesn't exist, redirect to 404
                val page = pages.values.firstOrNull() ?: return@get call.respond(HttpStatusCode.NotFound)
                // If exist redirect to default
                return@get call.respondRedirect("${call.request.uri}/${page.id}")
            }
        }

        call.respondHtml {
            renderFullPage(project, currentVersion, page)
        }
    }
}

private fun HTML.renderFullPage(project: ProjectData, version: Version, currentVersion: Page) {
    setupHead {

        link {
            href = "/static/css/docs_style.css"
            rel = "stylesheet"
        }

        link {
            href = "/static/css/docs_content.css"
            rel = "stylesheet"
        }

        link {
            href = "/static/css/themes/one_dark.css"
            rel = "stylesheet"
        }

        title { +"TrimphTeam | ${project.name}" }
    }

    body {

        classes = setOf("bg-docs-bg", "text-white", "overflow-y-auto overflow-x-hidden")

        sideBar(project, version, currentVersion)
        content(currentVersion)
        toast()

        script {
            src = "/static/scripts/script.js"
        }
    }
}

private fun FlowContent.content(page: Page) {
    // Content area
    div {

        classes = setOf("w-screen h-screen", "px-[36em]", "pt-12", "grid grid-cols-1 gap-4")

        // Actual content
        div {
            classes = setOf("h-full", "w-full")

            div {

                id = "content"
                classes = setOf("docs-content")

                unsafe {
                    raw(page.content)
                }
            }
        }

        // Footer
        footer {

            classes = setOf("text-xs", "text-center", "py-8", "self-end")
            +"Copyright © ${LocalDate.now().year}, TriumphTeam. All Rights Reserved."
        }
    }

    // Right side, bar area
    div {

        classes = setOf("fixed inset-y-0 right-0", "w-80 h-screen", "px-6", "py-16")

        div {
            classes = setOf("py-4", "px-2")
            a {
                href = page.summary.path
                classes = setOf("w-full", "text-white/75", "text-sm")
                +"Edit this page on GitHub"
            }
        }

        h1 {
            classes = setOf("text-2xl", "font-bold", "py-2")
            +"On this page"
        }

        ul {
            id = "summary"
            classes = setOf("text-white/75 text-lg", "summary")
            page.summary.entries.forEach { entries(it, true) }
        }
    }
}

private fun UL.entries(entry: SummaryEntry, initial: Boolean = false) {
    li {

        if (initial) {
            id = "summary-${entry.href}"
        }

        a {
            href = "#${entry.href}"
            +entry.literal
        }

        if (entry.children.isNotEmpty()) {
            ul {
                entry.children.forEach(::entries)
            }
        }
    }
}

private fun FlowContent.sideBar(project: ProjectData, version: Version, currentPage: Page) {
    div {

        classes = setOf(
            "fixed",
            "w-72",
            "h-screen",
            "flex flex-col",
            "gap-6",
            "px-4",
            "justify-items-center",
        )

        // Logo area
        div {
            classes = setOf("grid", "grid-cols-1", "gap-4", "w-full", "justify-items-center", "h-64")

            a {
                href = "/"
                img(src = "/static/images/logo.png", classes = "col-span-1 w-36")
            }

            div {
                classes = setOf("col-span-1", "text-center", "font-bold", "text-xl")

                h1 {
                    +project.name
                }
            }

            div {
                classes = setOf("col-span-1")

                dropDown(project.versions.keys, version.reference)
            }
        }

        search()

        div {
            classes = setOf(
                "w-full",
                "h-full",
                "px-4",
                "overflow-y-auto",
                "overflow-x-hidden",
                "sidebar-content",
            )

            div {

                classes = setOf(
                    "grid",
                    "grid-cols-1",
                    "gap-10",
                )

                version.navigation.groups.forEach { group ->
                    barHeader(group.header, group.pages, currentPage)
                }

                // Keeps it from overflowing on the bottom
                div {
                    classes = setOf("h-12")
                }
            }
        }

        div {
            classes = setOf(
                "w-full",
                "h-8",
            )
        }
    }
}

private fun FlowContent.barHeader(text: String, pages: List<Navigation.Page>, currentPage: Page) {
    div {

        h1 {
            classes = setOf("text-white", "text-xl", "font-bold")

            +text
        }

        pages.forEach { page ->
            page(page.header, page.id, page.id == currentPage.id)
        }
    }
}

private fun FlowContent.page(text: String, link: String, isCurrentPage: Boolean) {
    val color = if (isCurrentPage) {
        "text-primary"
    } else {
        "text-white/70"
    }
    div {

        classes = setOf("pt-2")

        a {
            href = link

            classes = setOf(color, "text-lg", "hover:text-primary", "transition ease-in-out delay-100")

            +text
        }
    }
}

private fun getProject(project: String): ProjectData? {

    val cached = projectCache.getIfPresent(project)
    if (cached != null) return cached

    return transaction {
        val projectEntity = ProjectEntity.findById(project) ?: return@transaction null

        val versions = DocVersionEntity.find { DocVersions.project eq projectEntity.id }.map { entity ->
            Version(
                reference = entity.id.value,
                navigation = entity.navigation,
                pages = PageEntity.find { (Pages.project eq projectEntity.id) and (Pages.version eq entity.id) }
                    .map { pageEntity ->
                        Page(
                            id = pageEntity.id.value,
                            content = pageEntity.content,
                            summary = pageEntity.summary,
                        )
                    }.associateBy(Page::id),
            )
        }.associateBy(Version::reference)

        ProjectData(
            id = projectEntity.id.value,
            name = projectEntity.name,
            versions = versions,
        ).also {
            projectCache.put(it.id, it)
        }
    }
}

public data class ProjectData(
    public val id: String,
    public val name: String,
    public val versions: Map<String, Version>,
)

public data class Version(
    public val reference: String,
    public val navigation: Navigation,
    public val pages: Map<String, Page>,
) {

    public data class Data(public val reference: String, public val stable: Boolean)
}

public data class Page(
    public val id: String,
    public val content: String,
    public val summary: PageSummary,
)
