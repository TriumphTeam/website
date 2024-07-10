import js.array.ReadonlyArray
import js.array.asList
import web.dom.ParentNode
import web.dom.observers.IntersectionObserver
import web.dom.observers.IntersectionObserverInit

private data class Section(
    val parentId: String,
    val element: web.dom.Element,
    val children: List<Section>,
)

private val intersectionOptions = object : IntersectionObserverInit {
    override var root: ParentNode? = null
    override var rootMargin: String? = "0px"
    override var threshold: ReadonlyArray<Double>? = arrayOf(1.0)
}

public fun main() {
    observer()
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

            if (onScreen.isEmpty()) return@IntersectionObserver

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
