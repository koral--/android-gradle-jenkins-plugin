package pl.droidsonroids.gradle.jenkins

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class ConnectedUiTestFunctionalTest {

	@Rule
	public TemporaryProjectFolder temporaryFolder = new TemporaryProjectFolder()

	@Test
	public void testConnectedUiTest() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('noTestableVariant.gradle', 'build.gradle')
		temporaryFolder.newFolder('src', 'main')
		temporaryFolder.copyResource('AndroidManifest.xml', 'src/main/AndroidManifest.xml')

		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments(Constants.CONNECTED_UI_TEST_TASK_NAME, "-P$Constants.UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.noMinify.name()}")
				.withPluginClasspath()
				.buildAndFail()

		assertThat(result.task(":$Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME")).isNotNull()
		assertThat(result.task(":$Constants.CONNECTED_CHECK_TASK_NAME")).isNotNull()
	}

}