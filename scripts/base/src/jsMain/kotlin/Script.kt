import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

private const val hiddenClass = "opacity-0"
private const val invisibleClass = "invisible"

public fun main() {
    // Show version dropdown
    showListener(
        buttonId = "version-select-button",
        elementId = "version-select",
    )
    // Hide the version dropdown
    hideListener(
        buttonId = "version-select-button",
        elementId = "version-select",
    )

    // Show search area
    showListener(
        buttonId = "searchbar-button",
        elementId = "search-area",
    ) {
        document.body?.addClass("opened-search")
    }
    // Hide the search area
    hideListener(
        buttonId = "searchbar-button",
        elementId = "search-area",
        ignoreElementId = "search-area-container",
    ) {
        document.body?.removeClass("opened-search")
    }
    copyCodeListener()
}

private fun copyCodeListener() {
    val toastElement = document.getElementById("toast")

    document.querySelectorAll("#copy").asList().forEach { element ->
        element.addEventListener("click", { event ->
            copyToClipboard(toastElement, event.target)
        })
    }
}

private fun copyToClipboard(toastElement: Element?, target: EventTarget?) {
    if (target == null) return
    if (target !is Element) return

    val parent = target.parentNode as? Element ?: return
    val content = parent.textContent ?: return
    window.navigator.clipboard.writeText(content).then {
        toastElement?.removeClass(hiddenClass)
        window.setTimeout({ toastElement?.addClass(hiddenClass) }, 1000)
    }
}

private fun showListener(
    buttonId: String,
    elementId: String,
    extra: () -> Unit = {},
) {

    document.addEventListener("click", { event: Event ->
        val buttonElement = document.getElementById(buttonId) ?: return@addEventListener
        if (!buttonElement.contains(event.target as Node)) return@addEventListener

        val target = document.getElementById(elementId)

        // Only show if hidden
        if (target?.hasClass(invisibleClass) == true) {
            target.removeClass(invisibleClass, hiddenClass)
            extra()
            return@addEventListener
        }

        // Don't hide if clicking on the links area
        if (target?.contains(event.target as Node) == true) return@addEventListener

        // Hide links
        target?.addClass(invisibleClass, hiddenClass)
    })
}

private fun hideListener(
    buttonId: String,
    elementId: String,
    ignoreElementId: String? = null,
    extra: () -> Unit = {},
) {

    document.addEventListener("click", { event: Event ->
        val buttonElement = document.getElementById(buttonId)
        val ignoreElement = ignoreElementId?.let { document.getElementById(it) }

        // Don't do global hide if clicking on the version element
        if (buttonElement?.contains(event.target as Node) == true) return@addEventListener
        if (ignoreElement?.contains(event.target as Node) == true) return@addEventListener

        val element = document.getElementById(elementId)

        // Hide if it's not hidden
        if (element?.hasClass(invisibleClass) == false) {
            element.addClass(invisibleClass, hiddenClass)
        }

        extra()
    })
}
