package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class TailwindCompile : DefaultTask() {

    @get:InputDirectory
    abstract val srcDir: DirectoryProperty

    @get:InputDirectory
    abstract val resourcesDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    private val ignoreList = "\${(!#'".toList()
    private val quotesRegex = "\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"".toRegex()

    @TaskAction
    fun create() {
        val probablyClasses = srcDir.get().asFile.walkTopDown().flatMap { file ->
            if (file.isDirectory) return@flatMap emptyList()
            val content = file.readText()
            val quotesInClass = quotesRegex.findAll(content)

            quotesInClass
                .map(MatchResult::value)
                .filter { string -> ignoreList.none { it in string } }
                .map { it.removePrefix("\"").removeSuffix("\"").trim() }
                .flatMap { string -> string.trim().split(" ") }
                .filter { it.length > 1 }
                .filterNot { it.startsWith("/") }
                .toList()
        }.toSet()

        val config = resourcesDir.get().asFile.resolve("static/tailwind.config.js")

        val output = outputDir.get().asFile

        output.resolve("tailwind.config.js").also { file ->
            if (!file.exists()) file.createNewFile()
        }.apply {
            writeText(config.readText().replace("tailwind.config", "module.exports"))
        }

        output.resolve("index.html").also { file ->
            if (!file.exists()) file.createNewFile()
        }.apply {
            writeText(HTML_TEMPLATE.trimIndent().replace(REPLACE_PLACEHOLDER, probablyClasses.joinToString(" ")))
        }
    }
}

private const val REPLACE_PLACEHOLDER = "{REPLACE}"

private const val HTML_TEMPLATE = """
   <!doctype html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="./output.css" rel="stylesheet">
    </head>
    
    <body>
        <h1 class="$REPLACE_PLACEHOLDER">
            Hello world!
        </h1>
    </body>
    </html> 
"""
