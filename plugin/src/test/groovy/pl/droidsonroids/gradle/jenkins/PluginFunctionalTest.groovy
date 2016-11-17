package pl.droidsonroids.gradle.jenkins

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test

import java.util.regex.Pattern

import static org.assertj.core.api.Assertions.assertThat
import static pl.droidsonroids.gradle.jenkins.JenkinsPlugin.UI_TEST_MODE_PROPERTY_NAME

class PluginFunctionalTest {

	@Rule
	public TemporaryProjectFolder mTemporaryFolder = new TemporaryProjectFolder()

	@Test
	public void testSetup() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('connectedSetupUiTest')
				.withPluginClasspath()
				.buildAndFail()
		assertThat(result.output).contains('No connected devices')
		assertThat(result.task(':cleanUiTestTempDir').outcome).isEqualTo(TaskOutcome.SUCCESS)
	}

	@Test
	public void testInstrumentationRunnerNotChangedWithoutUiTest() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('noTestableVariant.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertThat(result.output).doesNotMatch(Pattern.compile("Instrumentation test runner for.*"))
	}

	@Test
	public void testCustomUiTestInstrumentationRunner() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('noTestableVariant.gradle', 'build.gradle')
		mTemporaryFolder.projectFile('build.gradle') <<
				"""
		jenkinsTestable {
			testInstrumentationRunner 'test.example.Runner'
		}
				"""
		println mTemporaryFolder.projectFile('build.gradle').text
		def result = GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('projects', "-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.development.name()}")
				.withPluginClasspath()
				.build()

		assertThat(result.output).containsPattern('Instrumentation test runner for \\w+: test\\.example\\.Runner')
	}
}