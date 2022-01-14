package dev.triumphteam.backend.config

import dev.triumphteam.backend.func.create
import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Name
import me.mattstudios.config.annotations.Path

object Settings : SettingsHolder {

    @Path("repo")
    val REPO = create(Repo)

}

data class Repo(
    var name: String = "",

    @Name("latest-commit")
    var latestCommit: String = "",

    @Name("download")
    var downloadLink: String = "",

    @Name("github-path")
    var githubPath: String = "",
) {
    companion object : BeanFactory<Repo> {
        override fun createDefault(): Repo {
            return Repo()
        }
    }
}

interface BeanFactory<B> {
    fun createDefault(): B
}