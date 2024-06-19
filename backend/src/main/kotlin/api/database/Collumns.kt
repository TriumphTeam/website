package dev.triumphteam.backend.api.database

import dev.triumphteam.website.JsonSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.sql.Clob
import kotlin.reflect.KClass

public inline fun <reified T : Any> Table.serializable(name: String, serializer: KSerializer<T>): Column<T> =
    registerColumn(name, SerializableColumnType(T::class, serializer))

public inline fun <reified T : Any> Table.serializable(name: String): Column<T> =
    registerColumn(name, SerializableColumnType(T::class, JsonSerializer.json.serializersModule.serializer()))

@Suppress("UNCHECKED_CAST")
public class SerializableColumnType<T : Any>(
    private val klass: KClass<T>,
    private val serializer: KSerializer<T>,
) : ColumnType<T>() {

    override fun sqlType(): String = currentDialect.dataTypeProvider.textType()

    override fun notNullValueToDB(value: T): Any = when {
        klass.isInstance(value) -> JsonSerializer.json.encodeToString(serializer, value)
        else -> error("$value of ${value::class.qualifiedName} is not an instance of ${klass.simpleName}")
    }

    override fun valueFromDB(value: Any): T = when (value) {
        is Clob -> JsonSerializer.json.decodeFromString(serializer, value.characterStream.readText())
        is ByteArray -> JsonSerializer.json.decodeFromString(serializer, String(value))
        is String -> JsonSerializer.json.decodeFromString(serializer, value)
        else -> value as T
    }
}
