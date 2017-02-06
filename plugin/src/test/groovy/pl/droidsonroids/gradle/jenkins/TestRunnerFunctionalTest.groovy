package pl.droidsonroids.gradle.jenkins

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

import java.util.regex.Pattern

import static org.assertj.core.api.Assertions.assertThat
import static pl.droidsonroids.gradle.jenkins.Constants.UI_TEST_MODE_PROPERTY_NAME

class TestRunnerFunctionalTest {

	@Rule
	public TemporaryProjectFolder temporaryFolder = new TemporaryProjectFolder()

	@Test
    void testInstrumentationRunnerNotChangedWithoutUiTest() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('noTestableVariant.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertThat(result.output).doesNotMatch(Pattern.compile("Instrumentation test runner for.*"))
	}

	@Test
    void testCustomUiTestInstrumentationRunner() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('noTestableVariant.gradle', 'build.gradle')
		temporaryFolder.projectFile('build.gradle') <<
				"""
		uiTest {
			testInstrumentationRunner 'test.example.Runner'
		}
				"""
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects', "-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.minify.name()}")
				.withPluginClasspath()
				.build()

		assertThat(result.output).containsPattern('Instrumentation test runner for \\w+: test\\.example\\.Runner')
	}
}