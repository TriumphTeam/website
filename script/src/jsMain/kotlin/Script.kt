import js.array.ReadonlyArray
import js.array.asList
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
import web.dom.ParentNode
import web.dom.observers.IntersectionObserver
import web.dom.observers.IntersectionObserverInit

private data class Section(
    val parentId: String,
    val element: web.dom.Element,
    val children: List<Section>,
)

private const val hiddenClass = "opacity-0"
private const val invisibleClass = "invisible"

private val intersectionOptions = object : IntersectionObserverInit {
    override var root: ParentNode? = null
    override var rootMargin: String? = "0px"
    override var threshold: ReadonlyArray<Double>? = arrayOf(1.0)
}

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
    )
    // Hide the search area
    hideListener(
        buttonId = "searchbar-button",
        elementId = "search-area",
        ignoreElementId = "search-area-container",
    )
    copyCodeListener()
    observer()
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

private fun showListener(buttonId: String, elementId: String) {

    val buttonElement = document.getElementById(buttonId)

    buttonElement?.addEventListener("click", { event: Event ->
        val target = document.getElementById(elementId)

        // Only show if hidden
        if (target?.hasClass(invisibleClass) == true) {
            target.removeClass(invisibleClass, hiddenClass)
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
) {
    val buttonElement = document.getElementById(buttonId)
    val ignoreElement = ignoreElementId?.let { document.getElementById(it) }

    document.addEventListener("click", { event: Event ->
        // Don't do global hide if clicking on the version element
        if (buttonElement?.contains(event.target as Node) == true) return@addEventListener
        if (ignoreElement?.contains(event.target as Node) == true) return@addEventListener

        val element = document.getElementById(elementId)

        // Hide if it's not hidden
        if (element?.hasClass(invisibleClass) == false) {
            element.addClass(invisibleClass, hiddenClass)
        }
    })
}

private fun observer() {

    fun getSelections(element: web.dom.Element, parentId: String? = null): List<Section> {
        return element.children
            .asList()
            .filter { it.tagName.equals("section", true) }
            .map {
                val id = parentId ?: it.id
                Section(
                    parentId = id,
                    element = it,
                    children = getSelections(it, id),
                )
            }
    }

    fun flatten(section: Section): List<Section> {
        return listOf(section).plus(section.children.flatMap(::flatten))
    }

    val onScreen = mutableMapOf<String, Int>()
    val contentElement = web.dom.document.getElementById("content") ?: return
    val summaryElement = web.dom.document.getElementById("summary") ?: return
    val summaryElements = summaryElement.children.asList().filter { element ->
        element.id.startsWith("summary")
    }.associateBy(web.dom.Element::id)
    val sections = getSelections(contentElement)
    val flatSections = sections.flatMap(::flatten)

    var lastAdded: web.dom.Element? = null
    val observer = IntersectionObserver(
        callback = { entries, _ ->
            entries.forEach { entry ->
                val target = entry.target.id

                if (entry.isIntersecting) {
                    onScreen[target] = flatSections.indexOfFirst { it.element.id == target }
                } else {
                    onScreen.remove(target)
                }
            }

            val (topMost) = onScreen.minBy { it.value }
            val selected = flatSections.find { it.element.id == topMost }?.parentId
            val element = summaryElements["summary-$selected"] ?: return@IntersectionObserver

            if (element.id != lastAdded?.id) {
                lastAdded?.classList?.remove("summary-active")
            }

            lastAdded = element
            element.classList.add("summary-active")
        },
        options = intersectionOptions
    )

    flatSections.forEach { section ->
        observer.observe(section.element)
    }
}
