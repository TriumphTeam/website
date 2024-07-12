import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs = listOf(
                "-opt-in=" +
                        listOf(
                            "kotlin.RequiresOptIn",
                            "kotlin.time.ExperimentalTime",
                            "kotlin.ExperimentalStdlibApi",
                            "kotlin.io.path.ExperimentalPathApi",
                            "kotlin.contracts.ExperimentalContracts",
                            "kotlinx.coroutines.ExperimentalCoroutinesApi",
                            "kotlinx.serialization.InternalSerializationApi",
                            "kotlinx.serialization.ExperimentalSerializationApi",
                        ).joinToString(","),
            )
        }
    }
}
