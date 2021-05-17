package dev.triumphteam.backend

import com.zaxxer.hikari.HikariConfig
import dev.triumphteam.backend.config.Settings
import dev.triumphteam.markdown.SummaryParser
import me.mattstudios.config.SettingsManager
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.BulletList
import org.commonmark.node.Heading
import org.commonmark.node.ListItem
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths

val LOGGER: Logger = LoggerFactory.getLogger("backend")

fun main() {

    val parser = Parser.builder().build()
    val document = parser.parse(example)
    val html = HtmlRenderer.builder().build()
    //println(html.render(document))

    println(SummaryParser.parse(example))
    println(SummaryParser.parse(example))
    println(SummaryParser.parse(example))

    /*val dataSource = HikariDataSource(hikariConfig)
    Database.connect(dataSource)

    // Creates all the tables
    transaction {
        SchemaUtils.create(
            Projects,
            Indexes,
            Entries,
        )
    }*/

    val test = listOf(
        "Entry",
        "Entry 2",
        listOf("Sub entry"),
        "Entry 3",
        listOf("Sub entry", "Sub entry 2")
    )

    /*fun test(list: List<*>, id: EntityID<Int>? = null) {
        var lastId = id
        list.forEachIndexed { i, it ->

            if (it is List<*>) {
                test(it, lastId)
                return@forEachIndexed
            }

            if (it !is String) return@forEachIndexed

            transaction {
                lastId = Entries.insertAndGetId { entry ->
                    entry[index] = 1
                    entry[text] = it
                    entry[type] = 0u
                    entry[parent] = id
                    entry[position] = i.toUInt()
                }
            }
        }
    }*/

    /*repeat(10) {
        if (it == 9) sleep(TimeUnit.SECONDS.toMillis(60))
        val time = measureTimeMillis {
            transaction {
                Entries.select { Entries.index eq 1 }.forEach {
                    println(it)
                }
            }
        }
        println("Get took ${time}ms")
    }*/

/*
    embeddedServer(CIO, module = Application::module, port = 8000).start(true)*/
}

val CONFIG = SettingsManager
    .from(Paths.get("data", "config.yml"))
    .configurationData(Settings::class.java)
    .create()

// Hikari configuration from the config file
private val hikariConfig = HikariConfig().apply {
    val databaseConfig = CONFIG[Settings.DATABASE]
    dataSourceClassName = "org.mariadb.jdbc.MariaDbDataSource"
    jdbcUrl = "jdbc:mariadb://${databaseConfig.host}:${databaseConfig.port}/"
    username = databaseConfig.username
    password = databaseConfig.password
    isAutoCommit = false
    addDataSourceProperty("databaseName", databaseConfig.database)
}

private val example = """
            # Text entry
            
            * [Entry 1](test.md)
            * [Entry 2](test.md)
              * [Sub entry 1](test.md)
            * [Entry 3](test.md)
            
            # Text entry 2
            
            * [Entry 4](test.md)
              * [Sub entry 2](test.md)
              * [Sub entry 3](test.md)
            * [Entry 5](test.md)
        """.trimIndent()

class TestVisitor : AbstractVisitor() {

    val mainList = mutableListOf<Any>()
    private var sub: MutableList<String> = mutableListOf<String>()

    private var header = false
    private var list = false

    override fun visit(heading: Heading) {
        header = true
        visitChildren(heading)
        header = false
    }

    override fun visit(bulletList: BulletList) {
        sub.clear()
        visitChildren(bulletList)
        mainList.add(sub)
    }

    override fun visit(listItem: ListItem) {
        list = true
        visitChildren(listItem)
        list = false
    }

    override fun visit(text: Text) {
        if (header) {
            mainList.add(text.literal)
            return
        }

        if (list) {
            sub.add(text.literal)
            return
        }

        if (sub.isEmpty()) {
            mainList.add(text.literal)
            return
        }
        /*println(
             buildString {
                 append(text.literal)
                 if (header) append(" - ").append("H1")
                 if (topList) append(" - ").append("TOP")
                 if (middleList) append(" - ").append("MIDDLE")
             }
         )*/
    }

}