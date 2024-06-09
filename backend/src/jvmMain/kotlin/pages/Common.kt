package dev.triumphteam.backend.pages

import kotlinx.css.link
import kotlinx.css.meta
import kotlinx.css.script
import kotlinx.html.HEAD
import kotlinx.html.HTML
import kotlinx.html.HtmlTagMarker
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script

@HtmlTagMarker
public fun HTML.setupHead(extra: HEAD.() -> Unit = {}) {
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

        // Setup Tailwind
        script {
            src = "https://cdn.tailwindcss.com"
        }

        script {
            +"""
                tailwind.config = {
                  theme: {
                    extend: {
                      colors: {
                        primary: '#9B55BA',
                        'primary-light': '#BA6EDC',
                        'card-bg': '#181818',
                        'card-bg-secondary': '#151515',
                        'docs-bg': '#141417',
                        'search-bg': '#202023',
                      }
                    }
                  }
                }
            """.trimIndent()
        }
    }
}
