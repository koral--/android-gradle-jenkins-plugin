package pl.droidsonroids.gradle.jenkins

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static pl.droidsonroids.gradle.jenkins.JenkinsPlugin.DEFAULT_MOCK_WEB_SERVER_BASE_URL

public class MockWebServerUrlTest {

	@Test
	public void testDefaultProperty() throws Exception {
		def project = ProjectBuilder.builder().build()
		assertThat(project.hasProperty('pl.droidsonroids.jenkins.mockWebServerBaseUrl')).isFalse()

		applyMinimumBuildscriptBody(project)

		assertThat(project.property('pl.droidsonroids.jenkins.mockWebServerBaseUrl')).isEqualTo(DEFAULT_MOCK_WEB_SERVER_BASE_URL)
	}

	@Test
	public void testOverriddenProperty() throws Exception {
		def project = ProjectBuilder.builder().build()

		def url = 'http://test.test'
		project.ext.'pl.droidsonroids.jenkins.mockWebServerBaseUrl' = url

		applyMinimumBuildscriptBody(project)

		assertThat(project.property('pl.droidsonroids.jenkins.mockWebServerBaseUrl')).isEqualTo(url)
	}

	private void applyMinimumBuildscriptBody(Project project) {
		project.pluginManager.apply('pl.droidsonroids.jenkins')
		project.pluginManager.apply('com.android.application')

		project.android {
			defaultConfig {
				applicationId 'pl.droidsonroids.testapplication'
				signingConfig signingConfigs.jenkinsRelease
				minSdkVersion 1
			}
			buildToolsVersion '25.0.0'
			compileSdkVersion 25
		}
	}
}