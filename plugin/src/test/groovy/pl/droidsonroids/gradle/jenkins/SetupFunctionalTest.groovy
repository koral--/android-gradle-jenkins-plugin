package pl.droidsonroids.gradle.jenkins

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class SetupFunctionalTest {

	@Rule
	public TemporaryProjectFolder temporaryFolder = new TemporaryProjectFolder()

	@Test
	public void testSetup() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments(Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME, "-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.noMinify.name()}")
				.withPluginClasspath()
				.buildAndFail()
		assertThat(result.output).contains('No connected devices')
	}

}