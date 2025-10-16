package dev.triumphteam.website.scripting.gui

import dev.triumphteam.gui.builder.BaseGuiBuilder
import dev.triumphteam.gui.kotlin.builder.AbstractKotlinGuiBuilder
import dev.triumphteam.gui.settings.GuiSettings

public object DocsGuiSettings : GuiSettings<DocsPlayer, DocsItemStack, DocsGuiSettings>()

public class DocsGuiBuilder : BaseGuiBuilder<DocsGuiBuilder, DocsPlayer, DocsGui, DocsItemStack, DocsContainerType>(
    DocsGuiSettings, DocsContainerType.Chest
) {

    override fun build(): DocsGui {
        return DocsGui(title)
    }
}

public class DocsKotlinGuiBuilder @PublishedApi internal constructor() :
    AbstractKotlinGuiBuilder<DocsGuiBuilder, DocsPlayer, DocsGui, DocsItemStack, DocsContainerType>(DocsGuiBuilder()) {

    @PublishedApi
    internal fun build(): DocsGui = backing().build()
}

public inline fun buildGui(builder: DocsKotlinGuiBuilder.() -> Unit): DocsGui {
    return DocsKotlinGuiBuilder().apply(builder).build()
}
