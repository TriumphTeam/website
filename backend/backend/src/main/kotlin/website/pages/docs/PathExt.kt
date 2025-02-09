package dev.triumphteam.backend.website.pages.docs

import io.ktor.http.Parameters

public fun Parameters.extractDocsPath(): DocsPath? {
    val params = getAll("param") ?: return null
    if (params.size > 3) return null

    var version: String? = null

    val project: String?
    val page: String?

    val first = params.getOrNull(0) ?: return null
    if (first[0].isDigit()) {
        version = first
        project = params.getOrNull(1)
        page = params.getOrNull(2)
    } else {
        project = first
        page = params.getOrNull(1)
    }

    return DocsPath(
        version = version,
        project = project ?: return null,
        page = page,
    )
}

public data class DocsPath(
    public val version: String?,
    public val project: String,
    public val page: String?,
)
