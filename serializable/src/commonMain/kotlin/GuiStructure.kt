@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@Serializable
public data class GuiData(
    public val title: String,
)

@Serializable
public sealed interface GuiDataContainerType {

    @Serializable
    public data class Chest(public val rows: Int) : GuiDataContainerType
}

