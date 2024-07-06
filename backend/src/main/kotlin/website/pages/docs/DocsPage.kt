package dev.triumphteam.backend.website.pages.docs

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.triumphteam.backend.api.SearchDocument
import dev.triumphteam.backend.api.database.DocVersionEntity
import dev.triumphteam.backend.api.database.DocVersions
import dev.triumphteam.backend.api.database.PageEntity
import dev.triumphteam.backend.api.database.Pages
import dev.triumphteam.backend.api.database.ProjectEntity
import dev.triumphteam.backend.api.projectIndex
import dev.triumphteam.backend.meilisearch.Meili
import dev.triumphteam.backend.website.pages.createIconPath
import dev.triumphteam.backend.website.pages.docs.components.DropdownOption
import dev.triumphteam.backend.website.pages.docs.components.dropDown
import dev.triumphteam.backend.website.pages.docs.components.noResults
import dev.triumphteam.backend.website.pages.docs.components.search
import dev.triumphteam.backend.website.pages.docs.components.searchArea
import dev.triumphteam.backend.website.pages.docs.components.searchResult
import dev.triumphteam.backend.website.pages.docs.components.toast
import dev.triumphteam.backend.website.pages.setupHead
import dev.triumphteam.backend.website.respondHtmlCached
import dev.triumphteam.website.highlightWord
import dev.triumphteam.website.project.Navigation
import dev.triumphteam.website.project.Page
import dev.triumphteam.website.trimAround
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.css.Color
import kotlinx.css.CssBuilder
import kotlinx.css.backgroundColor
import kotlinx.css.borderColor
import kotlinx.css.color
import kotlinx.css.properties.s
import kotlinx.css.transitionDuration
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
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.styleLink
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.html.unsafe
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private val projectCache: Cache<String, ProjectData> = Caffeine.newBuilder()
    .expireAfterWrite(5.minutes.toJavaDuration())
    .build()

public fun Routing.docsRoutes(meili: Meili, developmentMode: Boolean) {

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

        call.respondHtmlCached(cacheId(project, currentVersion, page)) {
            renderFullPage(developmentMode, project, currentVersion, page)
        }
    }

    get("/search") {

        fun HTML.respondEmpty(query: String = "") {
            body {
                noResults(query)
            }
        }

        val query = call.request.queryParameters["q"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val projectParam = call.request.queryParameters["p"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val versionParam = call.request.queryParameters["v"] ?: return@get call.respond(HttpStatusCode.BadRequest)

        if (query.isBlank()) {
            return@get call.respondHtml {
                respondEmpty()
            }
        }

        val project = getProject(projectParam) ?: return@get call.respond(HttpStatusCode.NotFound)
        val pages = project.versions[versionParam]?.pages ?: return@get call.respond(HttpStatusCode.NotFound)

        val result =
            meili.client.index(projectIndex(projectParam, versionParam)).search<SearchDocument>(query, limit = 5)
                .mapNotNull { document ->
                    val page = pages[document.pageId] ?: return@mapNotNull null
                    val summary = page.summary.find { it.href == document.anchor } ?: return@mapNotNull null

                    val queryWords = query.split(" ")
                    val trimmedDescription = summary.terms.joinToString(" ").trimAround(queryWords.first(), 50)

                    SearchResult(
                        title = "${page.title} | ${summary.literal}",
                        description = trimmedDescription.highlightWord(queryWords),
                        link = "/docs/$versionParam/$projectParam/${page.id}#${document.anchor}",
                    )
                }

        if (result.isEmpty()) {
            return@get call.respondHtml {
                respondEmpty(query)
            }
        }

        return@get call.respondHtml {
            body {
                result.forEach { result ->
                    searchResult(result.title, result.description, result.link)
                }
            }
        }
    }
}

private fun HTML.renderFullPage(
    developmentMode: Boolean,
    project: ProjectData,
    version: Version,
    currentPage: ProjectPage,
) {
    setupHead(developmentMode) {

        styleLink("/static/css/docs_style.css")
        styleLink("/static/css/docs_content.css")
        styleLink("/static/css/themes/one_dark.css")

        script {
            src = "https://unpkg.com/htmx.org@2.0.0"
        }

        val title = "TrimphTeam | ${project.name} - ${currentPage.title}"
        // TODO: Replace with final URL, sucks that it can't be relative
        val image = "https://new.triumphteam.dev/assets/${project.id}/${version.reference}/${currentPage.id}/banner.png"

        meta {
            name = "description"
            content = currentPage.subTitle
        }

        // Facebook Meta Tags
        meta {
            attributes["property"] = "og:type"
            content = "article"
        }

        meta {
            attributes["property"] = "og:title"
            content = title
        }

        meta {
            attributes["property"] = "og:description"
            content = currentPage.subTitle
        }

        meta {
            attributes["property"] = "og:title"
            content = title
        }

        meta {
            attributes["property"] = "og:image"
            content = image
        }

        // Twitter Meta Tags
        meta {
            name = "twitter:card"
            content = "summary_large_image"
        }

        meta {
            name = "twitter:title"
            content = title
        }

        meta {
            name = "twitter:description"
            content = currentPage.subTitle
        }

        meta {
            name = "twitter:image"
            content = image
        }

        title { +title }

        style {
            +CssBuilder().apply {
                rule(".project-color-bg") {
                    backgroundColor = Color(project.color)
                }

                rule(".project-color") {
                    color = Color(project.color)
                }

                rule(".project-color-hover") {
                    transitionDuration = 0.3.s
                }

                rule(".project-color-border") {
                    transitionDuration = 0.3.s
                }

                rule(".project-color-hover:hover") {
                    color = Color(project.color)
                }

                rule(".project-color-border:hover") {
                    borderColor = Color(project.color)
                }

                rule(".docs-content a") {
                    color = Color(project.color)
                    transitionDuration = 0.3.s
                }
                rule(".docs-content a:hover") {
                    color = Color("${project.color}C8")
                }

                rule(".summary-active *") {
                    color = Color(project.color)
                }
            }.toString()
        }
    }

    body {

        classes = setOf("bg-docs-bg", "text-white", "overflow-y-auto overflow-x-hidden")

        // Must be first here
        searchArea(project.id, version.reference)

        sideBar(project, version, currentPage)
        content(currentPage)
        toast()

        script {
            src = "/static/scripts/script.js"
        }
    }
}

private fun FlowContent.content(page: ProjectPage) {
    // Content area
    div {

        classes = setOf("w-screen h-screen", "px-[36em]", "pt-12", "grid grid-cols-1 gap-4")

        // Actual content
        div {
            classes = setOf("h-full", "w-full")

            div {
                classes = setOf(
                    "absolute -z-10 w-[1700px] h-96 left-0 right-0 top-0 mx-auto",
                    "bg-dots bg-cover opacity-85 pointer-events-none",
                )
            }

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
                href = page.path
                classes =
                    setOf("w-full", "text-white/75", "text-sm", "transition ease-in-out delay-100 project-color-hover")
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
            page.summary.forEach { entries(it, true) }
        }
    }
}

private fun UL.entries(entry: Page.Summary, initial: Boolean = false) {
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

private fun FlowContent.sideBar(project: ProjectData, version: Version, currentPage: ProjectPage) {
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

            div {
                classes = setOf("flex items-center h-36 pt-4")

                a {
                    href = "/"
                    img(src = project.icon, classes = "col-span-1 w-28")
                }
            }

            div {
                classes = setOf("col-span-1", "text-center", "font-bold", "text-xl")

                h1 {
                    +project.name
                }
            }

            div {
                classes = setOf("col-span-1")

                dropDown(
                    options = project.versions.values.map { ver ->
                        DropdownOption(
                            text = ver.reference,
                            link = "/docs/${ver.reference}/${project.id}",
                            selected = ver.reference == version.reference,
                        )
                    }
                )
            }
        }

        div {
            id = "searchbar-button"

            classes = setOf("cursor-pointer")
            search(enabled = false)
        }

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

private fun FlowContent.barHeader(text: String, pages: List<Navigation.Page>, currentPage: ProjectPage) {
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
    div {

        classes = setOf("pt-2", "text-white/70")

        a {

            href = link

            classes = setOf(
                "text-lg",
                "transition ease-in-out delay-100",
                "project-color-hover",
            ).plus(if (isCurrentPage) "project-color" else "")

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
                reference = entity.reference,
                navigation = entity.navigation,
                stable = entity.stable,
                pages = PageEntity.find { (Pages.project eq projectEntity.id) and (Pages.version eq entity.id) }
                    .map { pageEntity ->
                        ProjectPage(
                            id = pageEntity.pageId,
                            content = pageEntity.content,
                            path = pageEntity.path,
                            summary = pageEntity.summary,
                            title = pageEntity.title,
                            subTitle = pageEntity.subTitle,
                        )
                    }.associateBy(ProjectPage::id),
            )
        }.associateBy(Version::reference)

        ProjectData(
            id = projectEntity.id.value,
            name = projectEntity.name,
            icon = createIconPath(projectEntity.id.value),
            color = projectEntity.color,
            versions = versions,
        ).also {
            projectCache.put(it.id, it)
        }
    }
}

public data class ProjectData(
    public val id: String,
    public val name: String,
    public val icon: String,
    public val color: String,
    public val versions: Map<String, Version>,
)

public data class Version(
    public val reference: String,
    public val navigation: Navigation,
    public val stable: Boolean,
    public val pages: Map<String, ProjectPage>,
) {

    public data class Data(public val reference: String, public val stable: Boolean)
}

public data class ProjectPage(
    public val id: String,
    public val content: String,
    public val path: String,
    public val summary: List<Page.Summary>,
    public val title: String,
    public val subTitle: String,
)

private fun cacheId(project: ProjectData, version: Version, page: ProjectPage): String {
    return "${project.id}:${version.reference}:${page.id}"
}

@Serializable
private data class SearchResult(
    val title: String,
    val description: String,
    val link: String,
)
