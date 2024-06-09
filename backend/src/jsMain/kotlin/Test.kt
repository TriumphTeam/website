import kotlinx.browser.document
import org.w3c.dom.Node
import org.w3c.dom.events.Event

public fun main() {
    val hiddenClass = "hidden"
    val dropdownId = "version-select"
    val buttonId = "version-select-button"

    val buttonElement = document.getElementById(buttonId)

    buttonElement?.addEventListener("click", { event: Event ->
        val target = document.getElementById(dropdownId)
        val classes = target?.classList

        // Only show if hidden
        if (classes?.contains(hiddenClass) == true) {
            classes.remove(hiddenClass)
            return@addEventListener
        }

        // Don't hide if clicking on the links area
        if (target?.contains(event.target as Node) == true) return@addEventListener

        // Hide links
        classes?.add("hidden")
    })

    document.addEventListener("click", { event: Event ->
        // Don't do global hide if clicking on the version element
        if (buttonElement?.contains(event.target as Node) == true) return@addEventListener

        val element = document.getElementById(dropdownId)

        // Hide if it's not hidden
        val classes = element?.classList
        if (classes?.contains("hidden") == false) {
            classes.add("hidden")
        }
    })
}
