package dev.triumphteam.frontend.pages.docs

import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PROJECT_PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.VERSION_PROJECT_VARIABLE
import dev.triumphteam.horizon.router.Route
import dev.triumphteam.horizon.router.RouteVariablesUpdateResult
import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.horizon.state.policy.StateMutationPolicy
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import kotlin.reflect.KProperty

public data class DocsRouteVariables(
    public val version: String?,
    public val project: String,
    public val page: String,
)

public data class ProjectRouteVariable(
    public val version: String?,
    public val project: String,
) {
    public fun createPath(): String {
        if (version == null) return project
        return "$version/$project"
    }
}

public class DocsRoute(
    version: String?,
    project: String,
    page: String,
) : Route {

    public companion object {
        public const val VERSION_PROJECT_VARIABLE: String = "versionProject"
        public const val PROJECT_PAGE_VARIABLE: String = "projectPage"
        public const val PAGE_VARIABLE: String = "page"
    }

    public val projectState: RouteVariableState<ProjectRouteVariable> =
        RouteVariableState(ProjectRouteVariable(version, project))
    public val pageState: RouteVariableState<String> = RouteVariableState(page)

    override fun updateVariables(variables: Map<String, String>): RouteVariablesUpdateResult {
        val variables = createRouteVariables(variables) ?: return RouteVariablesUpdateResult.ERROR

        val projectResult = projectState.setValue(ProjectRouteVariable(variables.version, variables.project))
        val pageResult = pageState.setValue(variables.page)

        if (projectResult || pageResult) RouteVariablesUpdateResult.UPDATED
        return RouteVariablesUpdateResult.NOT_UPDATED
    }
}

public class RouteVariableState<T>(initialValue: T) : AbstractState<T>(), MutableState<T> {

    private val mutationPolicy: StateMutationPolicy<T> = StructureEqualityPolicy()

    internal var value: T = initialValue
        private set

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        setValue(value)
    }

    internal fun setValue(value: T): Boolean {
        val shouldMutate = mutationPolicy.shouldMutate(this.value, value)
        if (!shouldMutate) return false
        this.value = value
        update()
        return true
    }

    override fun toString(): String {
        return "[$value]"
    }
}

public fun provideDocsRoute(initialVariables: Map<String, String>): DocsRoute? {
    val variables = createRouteVariables(initialVariables) ?: return null
    return DocsRoute(variables.version, variables.project, variables.page)
}

private fun createRouteVariables(variables: Map<String, String>): DocsRouteVariables? {
    val versionProject = variables[VERSION_PROJECT_VARIABLE]
    val projectPage = variables[PROJECT_PAGE_VARIABLE]
    val page = variables[PAGE_VARIABLE]

    return DocsRouteVariables(
        version = if (page == null) null else versionProject,
        project = (if (page == null) versionProject else projectPage) ?: return null,
        page = (page ?: projectPage) ?: return null,
    )
}
