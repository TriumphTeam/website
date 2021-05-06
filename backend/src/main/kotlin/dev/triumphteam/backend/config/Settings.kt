package dev.triumphteam.backend.config

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Settings : SettingsHolder {

    @Path("repo.url")
    val REPO = Property.create("")

    @Path("repo.latest-commit")
    val LATEST_COMMIT = Property.create("")

    @Path("repo.download")
    val REPO_DOWNLOAD = Property.create("")

}