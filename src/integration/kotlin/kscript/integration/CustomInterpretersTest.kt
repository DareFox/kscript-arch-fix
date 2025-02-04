package kscript.integration

import kscript.integration.tools.TestContext.copyToExecutablePath
import kscript.integration.tools.TestAssertion.any
import kscript.integration.tools.TestAssertion.verify
import kscript.integration.tools.TestContext.projectDir
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class CustomInterpretersTest : TestBase {
    @Test
    @Tag("posix")
    fun `Execute mydsl as interpreter`() {
        verify("mydsl \"println(foo)\"", 0, "bar\n", any())
    }

    @Test
    @Tag("posix")
    fun `Execute mydsl test with deps`() {
        verify("$projectDir/test/resources/custom_dsl/mydsl_test_with_deps.kts", 0, "foobar\n", any())
    }

    companion object {
        init {
            copyToExecutablePath("test/resources/custom_dsl/mydsl")
        }
    }
}
