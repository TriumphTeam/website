package dev.triumphteam.website.scripting

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCollectedData
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptConfigurationRefinementContext
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.collectedAnnotations
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.dependencies
import kotlin.script.experimental.api.onSuccess
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.api.with
import kotlin.script.experimental.dependencies.CompoundDependenciesResolver
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.FileSystemDependenciesResolver
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.dependencies.resolveFromScriptSourceAnnotations
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
    compilationConfiguration = ScriptWithMavenDepsConfiguration::class,
)
public abstract class SimpleScript

public object ScriptWithMavenDepsConfiguration : ScriptCompilationConfiguration(
    {
        // adds implicit import statements (in this case `import kotlin.script.experimental.dependencies.DependsOn`, etc.)
        // to each script on compilation
        defaultImports(DependsOn::class, Repository::class)

        jvm {
            // the dependenciesFromCurrentContext helper function extracts the classpath from current thread classloader
            // and take jars with mentioned names to the compilation classpath via `dependencies` key.
            // to add the whole classpath for the classloader without check for jar presence, use
            // `dependenciesFromCurrentContext(wholeClasspath = true)`
            dependenciesFromCurrentContext(
                "scripting", // script library jar name
                "kotlin-scripting-dependencies" // DependsOn annotation is taken from this jar
            )
            compilerOptions("-jvm-target", "21")
        }
        // section that callbacks during compilation
        refineConfiguration {
            // the callback called when any of the listed file-level annotations are encountered in the compiled script
            // the processing is defined by the `handler`, that may return refined configuration depending on the annotations
            onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
        }
    }
)

private val resolver = CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver())

// The handler that is called during script compilation in order to reconfigure compilation on the fly
public fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
        ?: return context.compilationConfiguration.asSuccess() // If no action is performed, the original configuration should be returned
    return runBlocking {
        // resolving maven artifacts using annotation arguments
        resolver.resolveFromScriptSourceAnnotations(annotations)
    }.onSuccess {
        context.compilationConfiguration.with {
            // updating the original configurations with the newly resolved artifacts as compilation dependencies
            dependencies.append(JvmDependency(it))
        }.asSuccess()
    }
}
