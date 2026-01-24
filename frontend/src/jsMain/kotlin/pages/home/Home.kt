package dev.triumphteam.frontend.pages.home

import dev.triumphteam.frontend.components.simpleButton
import dev.triumphteam.frontend.pages.home.components.inventoryIllustration
import dev.triumphteam.frontend.tailwind.PURPLE_SHADOW
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.a
import dev.triumphteam.horizon.html.button
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.footer
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.html.h2
import dev.triumphteam.horizon.html.h3
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.html.p
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.router.Route

public fun FlowContent.home(route: Route) {
    div(className = "w-screen min-h-screen bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]") {
        // Hero Section
        div(className = "grid grid-rows-1 w-full h-[85vh] justify-items-center content-center") {
            div(className = "row-span-1 self-center") {
                div(className = "flex gap-16") {
                    div(className = "flex flex-col gap-2") {
                        img(className = "self-center h-32", src = "/assets/logo.png", alt = "Logo")
                        h1(className = "text-white text-center text-6xl font-bold uppercase $PURPLE_SHADOW") {
                            text("Triumph Team")
                        }
                        h2(className = "text-white/50 text-center") {
                            text("Making libraries for your block game projects")
                        }

                        div(className = "flex justify-center gap-4") {
                            simpleButton {
                                i(className = "bxl bx-discord-alt")
                            }

                            simpleButton {
                                i(className = "bxl bx-github")
                            }
                        }
                    }

                    inventoryIllustration()
                }
            }
        }

        // Projects Section
        div(className = "w-full py-16") {
            div(className = "flex flex-col items-center gap-12") {
                div(className = "flex flex-col items-center gap-2") {
                    h2(className = "text-white text-4xl font-bold uppercase $PURPLE_SHADOW") {
                        text("Projects")
                    }
                    p(className = "text-white/50 text-center") {
                        text("Explore our libraries")
                    }
                }

                div(className = "flex flex-wrap gap-6 max-w-7xl mx-auto justify-center") {
                    projectCard(
                        Project(
                            logo = "/assets/project-logo.png",
                            title = "Triumph CMDs",
                            description = "Modern command framework for Minecraft",
                            versions = listOf(
                                ProjectVersion("v1.0", "/docs/cmds/v1.0"),
                                ProjectVersion("v2.0", "/docs/cmds/v2.0"),
                                ProjectVersion("v3.0", "/docs/cmds/v3.0"),
                            ),
                        ),
                    )
                    projectCard(
                        Project(
                            logo = "/assets/gui-logo.png",
                            title = "Triumph GUI",
                            description = "Beautiful inventory GUIs made simple",
                            versions = listOf(
                                ProjectVersion("v3.0", "/docs/gui/v3.0"),
                                ProjectVersion("v3.1", "/docs/gui/v3.1"),
                            ),
                        ),
                    )
                }
            }
        }
    }

    // Footer
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

public data class ProjectVersion(
    val name: String,
    val url: String,
)

public data class Project(
    val logo: String,
    val title: String,
    val description: String? = null,
    val versions: List<ProjectVersion>,
)


public fun FlowContent.projectCard(project: Project) {
    div(className = "w-80 group relative overflow-hidden bg-dark-surface backdrop-blur-sm rounded-xl border border-dark-accent hover:border-primary/50 transition-all duration-300 hover:shadow-2xl hover:shadow-primary/20") {
        div(className = "p-6") {
            // Logo with subtle glow
            div(className = "mb-4 flex justify-center") {
                div(className = "relative") {
                    div(className = "absolute inset-0 bg-primary/10 group-hover:bg-primary/20 blur-2xl rounded-full transition-all duration-300")
                    img(
                        className = "relative w-16 h-16 object-contain",
                        src = project.logo,
                        alt = "${project.title} logo",
                    )
                }
            }

            // Title
            h3(className = "text-dark-text font-bold text-xl mb-2 text-center") {
                text(project.title)
            }

            // Description
            project.description?.let { desc ->
                p(className = "text-default-text text-sm mb-4 text-center leading-relaxed") {
                    text(desc)
                }
            } ?: run {
                div(className = "mb-4")
            }

            // Version buttons
            div(className = "flex flex-wrap gap-2 justify-center mt-4") {
                project.versions.forEach { version ->
                    a(
                        className = "group/btn relative px-4 py-1.5 bg-dark-surface-hover hover:bg-secondary text-default-text hover:text-dark-text rounded-lg text-xs font-medium transition-all duration-200 border border-dark-accent hover:border-primary/50",
                    ) {
                        // Button text
                        div(className = "relative") {
                            text(version.name)
                        }
                    }
                }
            }
        }

        // Subtle corner accent
        div(className = "absolute -bottom-16 -right-16 w-32 h-32 bg-primary/5 group-hover:bg-primary/10 rounded-full blur-2xl pointer-events-none transition-all duration-300")
    }
}
