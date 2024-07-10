package dev.triumphteam.backend.website.pages

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.h1
import kotlinx.html.meta
import kotlinx.html.title

public fun HTML.respondNotFound(developmentMode: Boolean) {
    setupHead(developmentMode) {
        meta {
            name = "og:type"
            content = "article"
        }

        meta {
            name = "og:title"
            content = "TriumphTeam | Not Found"
        }

        meta {
            name = "og:image"
            content = "https://triumphteam.dev/static/images/banner_not_found.png"
        }

        title("TriumphTeam")
    }

    body {

        classes = setOf(
            "w-screen h-screen",
            "bg-docs-bg",
            "text-[20em] text-white/50",
            "flex justify-center items-center",
        )

        h1 {
            +"404"
        }
    }
}
