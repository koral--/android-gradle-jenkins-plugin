package pl.droidsonroids.gradle.jenkins

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
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
				.withArguments('connectedSetupUiTest')
				.withPluginClasspath()
				.buildAndFail()
		assertThat(result.output).contains('No connected devices')
		assertThat(result.task(':cleanUiTestTempDir').outcome).isEqualTo(TaskOutcome.SUCCESS)
	}

}