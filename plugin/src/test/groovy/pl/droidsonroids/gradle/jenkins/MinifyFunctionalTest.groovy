package pl.droidsonroids.gradle.jenkins

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static pl.droidsonroids.gradle.jenkins.Constants.UI_TEST_MODE_PROPERTY_NAME

class MinifyFunctionalTest {

	@Rule
	public TemporaryProjectFolder temporaryFolder = new TemporaryProjectFolder()

	@Test
	public void testNoOverrideOnNoProperty() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		temporaryFolder.projectFile('build.gradle') <<
				"""
		uiTest {
			minifyEnabled true
		}
				"""
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertThat(result.output).doesNotContain("minifyEnabled for")
	}

	@Test
	public void testOverrideOnMinifyProperty() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		temporaryFolder.projectFile('build.gradle') <<
				"""
		uiTest {
			minifyEnabled true
		}
				"""
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects', "-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.minify.name()}")
				.withPluginClasspath()
				.build()
		assertThat(result.output).contains("minifyEnabled for debug set to true")
	}

	@Test
	public void testOverrideOnNoMinifyProperty() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		temporaryFolder.projectFile('build.gradle') <<
				"""
		android.buildTypes.debug.minifyEnabled true
		uiTest {
			minifyEnabled true
		}
				"""
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects', "-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.noMinify.name()}")
				.withPluginClasspath()
				.build()
		assertThat(result.output).contains("minifyEnabled for debug set to false")
	}

	@Test
	public void testNoOverrideOnNoMinifyPropertyAndNoExtension() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		temporaryFolder.projectFile('build.gradle') << '\n'
		temporaryFolder.projectFile('build.gradle') << 'android.buildTypes.debug.minifyEnabled true'

		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects', "-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.minify.name()}")
				.withPluginClasspath()
				.build()
		assertThat(result.output).contains("minifyEnabled for debug set to true")
	}
}