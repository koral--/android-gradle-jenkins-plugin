package pl.droidsonroids.gradle.jenkins

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class SetupFunctionalTest {

	@Rule
	public TemporaryProjectFolder temporaryFolder = new TemporaryProjectFolder()

	@Test
    void testSetup() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments(Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME, "-P$Constants.UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.noMinify.name()}")
				.withPluginClasspath()
				.buildAndFail()
		assertThat(result.output).contains('No connected devices')
	}

	@Test
    void testUiTestDependencies() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments(Constants.CONNECTED_UI_TEST_TASK_NAME, "-P$Constants.UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.noMinify.name()}", '-m')
				.withPluginClasspath()
				.build()

		assertThat(result.task(":$Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME")).isNotNull()
		assertThat(result.task(":$Constants.SPOON_TASK_NAME")).isNotNull()
		assertThat(result.task(":$Constants.CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME")).isNotNull()
	}

}