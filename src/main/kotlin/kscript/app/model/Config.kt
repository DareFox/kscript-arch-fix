package kscript.app.model

import java.nio.file.Path

data class Config(
    val selfName: String,
    val kscriptDir: Path,
    val customPreamble: String,
    val intellijCommand: String,
    val gradleCommand: String,
    val kotlinHome: Path?,
    val classPathSeparator: String,
    val separatorChar: Char,
    val homeDir: Path
) {
    companion object {
        fun builder() = ConfigBuilder()
    }
}
