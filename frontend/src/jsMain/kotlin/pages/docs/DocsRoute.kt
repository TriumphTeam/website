package dev.triumphteam.frontend.pages.docs

import dev.triumphteam.frontend.api.api
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PROJECT_PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.VERSION_PROJECT_VARIABLE
import dev.triumphteam.frontend.state.ApiCallState
import dev.triumphteam.frontend.state.apiCallState
import dev.triumphteam.horizon.router.Route
import dev.triumphteam.horizon.router.RouteVariablesUpdateResult
import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.horizon.state.policy.StateMutationPolicy
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import dev.triumphteam.website.serializable.PROJECT_ROUTE
import dev.triumphteam.website.serializable.ProjectVersion
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KProperty

public data class DocsRouteVariables(
    public val version: String?,
    public val project: String,
    public val page: String,
)

public data class ProjectRouteVariable(
    public val version: String?,
    public val project: String,
)

public class DocsRoute(
    scope: CoroutineScope,
    version: String?,
    project: String,
    page: String,
) : Route {

    public companion object {
        public const val VERSION_PROJECT_VARIABLE: String = "versionProject"
        public const val PROJECT_PAGE_VARIABLE: String = "projectPage"
        public const val PAGE_VARIABLE: String = "page"
    }

    private var projectRouteCache: ProjectRouteVariable = ProjectRouteVariable(version, project)
    public val projectState: ApiCallState<ProjectVersion> = apiCallState(parentCoroutine = scope) {
        getProjectVersion(project, version)
    }
    public val pageState: RouteVariableState<String> = RouteVariableState(page)

    override fun updateVariables(variables: Map<String, String>): RouteVariablesUpdateResult {
        val variables = createRouteVariables(variables) ?: return RouteVariablesUpdateResult.ERROR
        val newProjectRoute = ProjectRouteVariable(variables.version, variables.project)

        val updatedProject = when {
            // If the project/version are different, we update the state and the cache.
            projectRouteCache != newProjectRoute -> {
                // The cache is mostly here to avoid an api call if it is the same.
                projectRouteCache = newProjectRoute

                // Refresh the project state.
                projectState.refreshCall {
                    getProjectVersion(variables.project, variables.version)
                }

                // True because it's different.
                true
            }

            else -> false
        }

        val updatedPage = pageState.setValue(variables.page)

        if (updatedProject || updatedPage) return RouteVariablesUpdateResult.UPDATED
        return RouteVariablesUpdateResult.NOT_UPDATED
    }

    private suspend fun getProjectVersion(project: String, version: String?): ProjectVersion {
        return api.get(urlString = PROJECT_ROUTE) {
            parameter("project", project)
            parameter("version", version)
        }.body<ProjectVersion>()
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
        return "[$value](${listeners.size})"
    }
}

public fun provideDocsRoute(scope: CoroutineScope, initialVariables: Map<String, String>): DocsRoute? {
    val variables = createRouteVariables(initialVariables) ?: return null
    return DocsRoute(scope, variables.version, variables.project, variables.page)
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
