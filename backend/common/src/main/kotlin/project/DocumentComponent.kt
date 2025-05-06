package dev.triumphteam.website.project

public sealed interface DocumentComponent {

    public val children: List<DocumentComponent>
}

public data class HeaderComponent(override val children: List<DocumentComponent>) : DocumentComponent

public data object SeparatorComponent : DocumentComponent {
    override val children: List<DocumentComponent> = emptyList()
}


