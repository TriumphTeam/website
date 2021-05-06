package dev.triumphteam.backend.config

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Settings : SettingsHolder {

    @Path("repo-url")
    val REPO = Property.create("")

    @Path("latest-commit")
    val LATEST_COMMIT = Property.create("")

}