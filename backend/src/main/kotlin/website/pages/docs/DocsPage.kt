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
import kotlinx.html.h1
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.lang
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
import kotlin.collections.set
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
            else -> project.versions.values.find { it.recommended } ?: return@get call.respond(HttpStatusCode.NotFound)
        }

        val pages = currentVersion.pages
        val page = when {
            !paramPage.isNullOrEmpty() -> pages[paramPage] ?: return@get call.respond(HttpStatusCode.NotFound)
            else -> {
                // If the page doesn't exist, redirect to 404
                val page = pages[currentVersion.defaultPage] ?: return@get call.respond(HttpStatusCode.NotFound)
                // If exist redirect to default
                return@get call.respondRedirect("${call.request.uri.removeSuffix("/")}/${page.id}")
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
    lang = "en"

    setupHead(developmentMode) {

        styleLink("/static/css/docs_style.css")
        styleLink("/static/css/docs_content.css")
        styleLink("/static/css/themes/one_dark.css")

        script {
            src = "https://unpkg.com/htmx.org@2.0.0"
        }

        script {
            src = "/static/scripts/base.js"
        }

        val title = "TriumphTeam | ${project.name} - ${currentPage.title}"
        val image = "https://triumphteam.dev/assets/${project.id}/${version.reference}/${currentPage.id}/banner.png"

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
            unsafe {
                raw(
                    CssBuilder().apply {
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

                        rule(".project-color-bg-hover") {
                            transitionDuration = 0.3.s
                        }

                        rule(".project-color-hover:hover") {
                            color = Color(project.color)
                        }

                        rule(".project-color-border:hover") {
                            borderColor = Color(project.color)
                        }

                        rule(".project-color-bg-hover:hover") {
                            backgroundColor = Color(project.color)
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
                )
            }
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
            src = "/static/scripts/observer.js"
        }
    }
}

private fun FlowContent.content(page: ProjectPage) {
    // Content area
    div {

        classes = setOf("w-screen h-full", "pt-12")

        // Actual content
        div {
            classes = setOf(
                "h-full",
                "w-3/4 xl:w-3/6 2xl:w-3/5",
                "mx-auto",
            )

            div {
                classes = setOf(
                    "absolute -z-10 w-3/4 h-96 left-0 right-0 top-0 mx-auto",
                    "dots bg-center opacity-85 pointer-events-none",
                )
            }

            div {

                id = "content"
                classes = setOf("docs-content pb-12")
                attributes["hx-boost"] = "true"

                unsafe {
                    raw(page.content)
                }
            }
        }
    }

    // Right side, bar area
    div {

        classes = setOf(
            "opacity-0 xl:opacity-100",
            "fixed inset-y-0 right-0",
            "h-screen",
            "xl:w-60 2xl:w-72",
            "px-6 py-16",
        )

        div {
            classes = setOf("py-4", "px-2")
            a {
                href = page.path
                target = "_blank"
                classes =
                    setOf("w-full", "text-white/75", "text-sm", "transition ease-in-out delay-100 project-color-hover")
                +"Edit this page on GitHub"
            }
        }

        h1 {
            classes = setOf(
                "xl:text-xl 2xl:text-2xl",
                "font-bold",
                "py-2",
            )
            +"On this page"
        }

        ul {
            id = "summary"
            classes = setOf(
                "text-white/75",
                "xl:text-base 2xl:text-lg",
                "summary",
            )
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

        classes = setOf("absolute opacity-100 xl:opacity-0 top-0 ml-6 mt-6 text-2xl")
        i {
            id = "show-sidebar-button"
            classes = setOf("fa-solid fa-bars cursor-pointer")
        }
    }

    div {

        id = "side-bar"
        classes = setOf(
            "fixed z-10",
            "bg-docs-bg",
            "w-screen xl:w-60 2xl:w-72",
            "h-svh h-screen",
            "hidden xl:flex flex-col",
            "gap-4",
            "px-4",
            "justify-center",
        )

        div {
            classes = setOf(
                "absolute top-0 right-0 mr-8 mt-6 text-2xl cursor-pointer",
                "xl:opacity-0",
            )

            i {
                id = "hide-sidebar-button"
                classes = setOf("fa-solid fa-xmark")
            }
        }

        // Logo area
        div {
            classes = setOf(
                "grid",
                "grid-cols-1",
                "gap-4",
                "w-full",
                "justify-items-center",
            )

            div {
                classes = setOf("flex items-center h-36 pt-4")

                a {
                    href = "/"
                    img(src = project.icon, classes = "col-span-1 w-28 xl:w-24 2xl:w-28")
                }
            }

            div {
                classes = setOf("col-span-1", "text-center", "font-bold", "text-xl")

                h1 {
                    +project.name
                }
            }
        }

        dropDown(
            options = project.versions.values.map { ver ->
                DropdownOption(
                    text = ver.reference,
                    link = "/docs/${ver.reference}/${project.id}",
                    selected = ver.reference == version.reference,
                )
            }
        )

        div {
            id = "searchbar-button"

            classes = setOf("cursor-pointer")
            search(enabled = false)
        }

        div {
            classes = setOf(
                "px-4",
                "overflow-y-auto overflow-x-hidden overscroll-contain",
                "grow",
            )

            div {

                attributes["hx-boost"] = "true"
                classes = setOf(
                    "grid",
                    "grid-cols-1",
                    "gap-10",
                )

                version.navigation.groups.forEach { group ->
                    barHeader(group.header, group.pages, currentPage)
                }
            }
        }

        div {
            classes = setOf(
                "w-full",
                "h-16",
                "flex flex-col gap-1 justify-center items-center flex-none",
            )

            div {
                classes = setOf(
                    "w-56",
                    "flex justify-center items-center gap-2",
                )

                bottomButton("Discord", "fa-brands fa-discord", version.discord ?: project.discord)
                bottomButton("Github", "fa-brands fa-github", version.github)
                bottomButton("Javadocs", "fa-solid fa-book", version.javadocs)
            }

            div {
                classes = setOf("flex-none", "text-[0.5em]", "text-center", "py-2")
                +"Copyright © ${LocalDate.now().year}, TriumphTeam. All Rights Reserved."
            }
        }
    }
}

private fun FlowContent.bottomButton(tooltip: String, icon: String, link: String?) {
    a {

        link?.let { href = it }
        target = "_bank"
        if (link != null) {
            attributes["data-tooltip"] = tooltip
        }

        val conditional = if (link != null) {
            setOf("project-color-bg-hover")
        } else {
            setOf("text-zinc-500", "cursor-default")
        }

        classes = setOf(
            "w-1/3",
            "flex justify-center items-center",
            "bg-search-bg",
            "rounded-md",
            "p-2",
        ).plus(conditional)

        i { classes = setOf(icon) }
    }
}

private fun FlowContent.barHeader(text: String, pages: List<Navigation.Page>, currentPage: ProjectPage) {
    div {

        h1 {
            classes = setOf(
                "text-white",
                "xl:text-lg 2xl:text-xl",
                "font-bold",
            )

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

            id = "navigation-link"
            href = link

            classes = setOf(
                "xl:text-base 2xl:text-lg",
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
                recommended = entity.recommended,
                defaultPage = entity.defaultPage,
                github = entity.github,
                discord = entity.discord,
                javadocs = entity.javadocs,
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
            discord = projectEntity.discord,
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
    public val discord: String?,
    public val versions: Map<String, Version>,
)

public data class Version(
    public val reference: String,
    public val navigation: Navigation,
    public val stable: Boolean,
    public val recommended: Boolean,
    public val pages: Map<String, ProjectPage>,
    public val defaultPage: String,
    public val github: String?,
    public val discord: String?,
    public val javadocs: String?,
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
