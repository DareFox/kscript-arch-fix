package kscript.app.creator

import kscript.app.code.Templates
import kscript.app.model.Script
import kscript.app.model.ScriptType
import kscript.app.shell.Executor
import kscript.app.shell.FileUtils
import kscript.app.shell.OsPath
import kscript.app.shell.writeText

data class JarArtifact(val path: OsPath, val execClassName: String)

class JarArtifactCreator(private val executor: Executor) {

    fun create(basePath: OsPath, script: Script, resolvedDependencies: Set<OsPath>): JarArtifact {
        // Capitalize first letter and get rid of dashes (since this is what kotlin compiler is doing for the wrapper to create a valid java class name)
        // For valid characters see https://stackoverflow.com/questions/4814040/allowed-characters-in-filename
        val className = script.location.scriptName.replace("[^A-Za-z0-9]".toRegex(), "_").replaceFirstChar { it.titlecase() }
            // also make sure that it is a valid identifier by avoiding an initial digit (to stay in sync with what the kotlin script compiler will do as well)
            .let { if ("^[0-9]".toRegex().containsMatchIn(it)) "_$it" else it }

        // Define the entrypoint for the scriptlet jar
        val execClassName = if (script.location.scriptType == ScriptType.KTS) {
            "Main_${className}"
        } else {
            """${script.packageName.value}.${script.entryPoint?.value ?: "${className}Kt"}"""
        }

        val jarFile = basePath.resolve("scriplet.jar")
        val scriptFile = basePath.resolve(className + script.location.scriptType.extension)
        val execClassNameFile = basePath.resolve("scripletExecClassName.txt")

        execClassNameFile.writeText(execClassName)

        FileUtils.createFile(scriptFile, script.resolvedCode)

        val filesToCompile = mutableSetOf<OsPath>()
        filesToCompile.add(scriptFile)

        // create main-wrapper for kts scripts
        if (script.location.scriptType == ScriptType.KTS) {
            val wrapper = FileUtils.createFile(
                basePath.resolve("$execClassName.kt"), Templates.createWrapperForScript(script.packageName, className)
            )
            filesToCompile.add(wrapper)
        }

        executor.compileKotlin(jarFile, resolvedDependencies, filesToCompile, script.compilerOpts)

        return JarArtifact(jarFile, execClassName)
    }
}
