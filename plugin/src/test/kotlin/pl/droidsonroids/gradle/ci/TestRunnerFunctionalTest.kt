package pl.droidsonroids.gradle.ci

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.gradle.ci.Constants.UI_TEST_MODE_PROPERTY_NAME
import java.util.regex.Pattern

class TestRunnerFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryProjectFolder()

    @Before
    fun setUp() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("noTestableVariant.gradle", "build.gradle")
    }

    @Test
    fun `test instrumentation runner not changed without ui test`() {
        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withPluginClasspath()
                .build()
        assertThat(result.output).doesNotMatch(Pattern.compile("Instrumentation test runner for.*"))
    }

    @Test
    fun `custom ui test instrumentation runner applied`() {
        temporaryFolder.projectFile("build.gradle").appendText("""
		uiTest {
			testInstrumentationRunner "test.example.Runner"
		}""")
        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments("-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.minify.name}")
                .withPluginClasspath()
                .build()

        assertThat(result.output).containsPattern("Instrumentation test runner for \\w+: test\\.example\\.Runner")
    }
}