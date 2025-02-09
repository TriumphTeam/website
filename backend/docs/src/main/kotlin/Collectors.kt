package dev.triumphteam.website.docs

import dev.triumphteam.website.project.Navigation
import dev.triumphteam.website.project.Page

public abstract class Collector<T, C> {

    protected val elements: MutableList<T> = mutableListOf()

    public fun collect(element: T) {
        elements.add(element)
    }

    public abstract fun collection(): C
}

public class PageCollector : Collector<Page, List<Page>>() {

    override fun collection(): List<Page> {
        return elements.toList()
    }
}

public class NavigationCollector : Collector<Navigation.Group, Navigation>() {

    override fun collection(): Navigation {
        return Navigation(groups = elements)
    }
}
