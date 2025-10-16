package dev.triumphteam.website.scripting.gui

import dev.triumphteam.gui.BaseGui
import dev.triumphteam.gui.container.type.GuiContainerType
import dev.triumphteam.gui.slot.Slot
import dev.triumphteam.gui.title.GuiTitle

public object DocsPlayer

public object DocsItemStack

public interface DocsContainerType : GuiContainerType {

    public object Chest : DocsContainerType {
        override fun toSlot(row: Int, column: Int): Int {
            return 0
        }

        override fun toSlot(slot: Int): Slot {
            return Slot.of(0, 0)
        }

        override fun toTopInventory(slot: Int): Int {
            return 0
        }

        override fun toPlayerInventory(slot: Int): Int {
            return 0
        }

        override fun isPlayerInventory(slot: Int): Boolean {
            return true
        }
    }
}

public data class DocsGui(
    public val title: GuiTitle,
) : BaseGui<DocsPlayer> {

    override fun open(player: DocsPlayer): Nothing {
        throw UnsupportedOperationException("Not implemented nor needed for docs.")
    }
}
