package dev.triumphteam.backend.website.pages

import kotlinx.html.FlowContent
import kotlinx.html.HEAD
import kotlinx.html.HTML
import kotlinx.html.HtmlTagMarker
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script

@HtmlTagMarker
public fun HTML.setupHead(developmentMode: Boolean, extra: HEAD.() -> Unit = {}) {
    head {
        extra()

        link {
            rel = "icon"
            href = "/static/favicon.ico"
            type = "image/x-icon"
            sizes = "any"
        }

        link {
            href = "https://api.fontshare.com/v2/css?f[]=general-sans@300,500,700&display=swap"
            rel = "stylesheet"
        }

        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1.0"
        }

        // Setup font awesome
        script {
            src = "https://kit.fontawesome.com/14e77edfda.js"
        }

        if (developmentMode) {
            // Setup Tailwind
            script {
                src = "https://cdn.tailwindcss.com"
            }

            script {
                src = "/static/tailwind.config.js"
            }
        } else {
            link {
                href = "/static/css/tailwind.css"
                rel = "stylesheet"
            }
        }
    }
}

public fun FlowContent.backgroundBlob(properties: List<String>) {
    div {
        classes = setOf(
            "absolute -z-10",
            "blob-background",
            "bg-cover",
            "pointer-events-none",
        ).plus(properties)
    }
}

public fun createIconPath(projectId: String): String {
    return "/assets/${projectId}/icon.png"
}
