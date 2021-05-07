package dev.triumphteam.backend.config

import dev.triumphteam.backend.func.create
import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Name
import me.mattstudios.config.annotations.Path

object Settings : SettingsHolder {

    @Path("repo")
    val REPO = create(Repo)

    @Path("database")
    val DATABASE = create(SqlData)

}

data class Repo(
    var name: String = "",

    @Name("latest-commit")
    var latestCommit: String = "",

    @Name("download")
    var downloadLink: String = "",
) {
    companion object : BeanFactory<Repo> {
        override fun createDefault(): Repo {
            return Repo()
        }
    }
}

data class SqlData(
    var host: String = "",
    var port: Int = 3360,
    var database: String = "",
    var username: String = "",
    var password: String = "",
) {
    companion object : BeanFactory<SqlData> {
        override fun createDefault(): SqlData {
            return SqlData()
        }
    }
}

interface BeanFactory<B> {
    fun createDefault(): B
}