package dev.triumphteam.frontend.pages.home

import dev.triumphteam.frontend.components.simpleButton
import dev.triumphteam.frontend.pages.home.components.projects
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.button
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.footer
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.html.h2
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.html.p
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.router.Route

public fun FlowContent.home(route: Route) {
    div(className = "w-screen min-h-screen bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]") {

        // Gradient background
        div(className = "absolute inset-0 h-full w-full bg-transparent [background:radial-gradient(125%_125%_at_50%_10%,transparent_20%,#f5f5f5_70%)] dark:[background:radial-gradient(125%_125%_at_50%_10%,transparent_20%,#141417_70%)]")

        div(className = "relative grid grid-rows-1 w-full min-h-[85vh] justify-items-center content-center px-4 py-12 overflow-hidden") {
            // Main content (centered and on top)
            div(className = "relative z-10 row-span-1 self-center max-w-4xl w-full") {
                div(className = "flex flex-col items-center gap-6") {
                    // Logo with glow effect
                    div(className = "relative") {
                        div(className = "absolute inset-0 blur-3xl bg-purple-600/40 rounded-full animate-pulse") {}
                        img(
                            className = "relative h-32 lg:h-40 drop-shadow-2xl transition-transform hover:scale-105 duration-300",
                            src = "/assets/logo.png",
                            alt = "Logo",
                        )
                    }

                    // Main heading with enhanced gradient
                    h1(className = "text-white text-center text-6xl lg:text-8xl font-bold uppercase bg-gradient-to-br from-white via-purple-200 to-purple-400 bg-clip-text text-transparent [text-shadow:_0px_0px_80px_rgba(141,78,184,0.6)] leading-tight") {
                        text("Triumph Team")
                    }

                    // Subtitle with better spacing
                    h2(className = "text-white/80 text-center text-lg lg:text-xl font-light tracking-wide max-w-2xl px-4") {
                        text("Making libraries for your block game projects.")
                    }

                    // Social buttons with improved styling
                    div(className = "flex justify-center gap-4 mt-6") {
                        simpleButton {
                            i(className = "bxl bx-discord-alt")
                        }
                        simpleButton {
                            i(className = "bxl bx-github")
                        }
                    }

                    // Projects badges
                    projects()
                }
            }
        }
    }

    footer(className = "w-full border-t border-white/10 mt-24") {
        div(className = "max-w-7xl mx-auto px-6 py-8") {
            div(className = "flex justify-between items-center") {
                // Copyright
                p(className = "text-white/50 text-sm") {
                    text("© ${js("new Date().getFullYear()")} Triumph Team. All rights reserved.")
                }

                // Theme toggle button
                button(
                    className = "group relative px-4 py-2 bg-dark-surface-hover hover:bg-primary/10 rounded-lg border border-dark-accent hover:border-primary/50 transition-all duration-200",
                ) {
                    div(className = "flex items-center gap-2") {
                        i(className = "bx bx-moon text-white/70 group-hover:text-primary transition-colors duration-200")
                        span(className = "text-white/70 group-hover:text-white text-sm font-medium transition-colors duration-200") {
                            text("Theme")
                        }
                    }
                }
            }
        }
    }
}
