package dev.triumphteam.frontend

import dev.triumphteam.frontend.pages.home.home
import dev.triumphteam.horizon.app
import dev.triumphteam.horizon.html.FlowContent

public fun main() {
    app {
        index(FlowContent::home)

    }
}
