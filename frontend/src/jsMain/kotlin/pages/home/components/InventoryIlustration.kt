package dev.triumphteam.frontend.pages.home.components

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.Tag
import dev.triumphteam.horizon.html.div

public fun FlowContent.inventoryIllustration() {
    div(className = "flex justify-center opacity-60 transition-opacity duration-500 hover:opacity-80") {
        div(className = "flex flex-col gap-4") {
            // Top portion.
            inventorySection(27, listOf(4, 11, 13, 15, 20, 24))
            // Bottom portion.
            inventorySection(9, listOf(0, 4, 8))
        }
    }
}

private fun FlowContent.inventorySection(size: Int, selectedSlots: List<Int>) {
    div(className = "grid grid-cols-9 gap-2 p-6 bg-dark-accent/40 rounded-lg border border-light-accent/20 backdrop-blur-sm shadow-2xl shadow-black/50") {
        repeat(size) { index ->
            div(className = "w-14 h-14 bg-zinc-900/60 border border-light-accent/20 rounded transition-all duration-300 hover:border-primary/50 hover:bg-primary/10 shadow-lg") {
                if (index in selectedSlots) {
                    div(className = "w-full h-full bg-gradient-to-br from-primary/20 to-secondary/20 rounded animate-pulse")
                }
            }
        }
    }
}
