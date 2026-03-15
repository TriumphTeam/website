package dev.triumphteam.frontend.pages.home

import dev.triumphteam.frontend.components.DOTTED_BACKGROUND
import dev.triumphteam.frontend.components.simpleButton
import dev.triumphteam.frontend.internal.now
import dev.triumphteam.frontend.pages.home.components.projects
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.button
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.footer
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.html.h2
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.html.p

public fun FlowContent.home() {
    div(className = "w-screen min-h-screen $DOTTED_BACKGROUND select-none") {
        div(className = "absolute inset-0 h-full w-full bg-transparent [background:radial-gradient(125%_125%_at_50%_10%,transparent_20%,#141417_70%)]")

        div(className = "relative grid grid-rows-1 w-full min-h-[85vh] justify-items-center content-center px-4 py-12 overflow-hidden") {
            div(className = "relative z-10 row-span-1 self-center max-w-4xl w-full") {
                div(className = "flex flex-col items-center gap-6") {
                    div(className = "relative") {
                        div(className = "absolute inset-0 blur-3xl bg-purple-600/40 rounded-full") {}
                        img(
                            className = "relative h-32 lg:h-40 drop-shadow-2xl transition-transform hover:scale-105 duration-300",
                            src = "/assets/logo.png",
                            alt = "Logo",
                        )
                    }

                    h1(className = "text-dark-text-primary text-center text-6xl lg:text-8xl font-bold uppercase leading-tight") {
                        text("Triumph Team")
                    }

                    h2(className = "text-dark-text-primary/80 text-center text-lg lg:text-xl font-light tracking-wide max-w-2xl px-4") {
                        text("Making libraries for your block game projects.")
                    }

                    div(className = "flex justify-center gap-4 mt-6") {
                        simpleButton {
                            i(className = "bxl bx-discord-alt")
                        }
                        simpleButton {
                            i(className = "bxl bx-github")
                        }
                    }

                    projects()
                }
            }
        }
    }

    footer(className = "w-full border-t border-white/10") {
        div(className = "max-w-7xl mx-auto px-6 py-8") {
            div(className = "flex justify-center items-center") {
                // Copyright
                p(className = "text-dark-text-primary/50 text-sm") {
                    text("Copyright © 2020-${now.getFullYear()}, TriumphTeam. All Rights Reserved.")
                }
            }
        }
    }
}
