package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

public class PluginApplyExceptionTest {

	@Test(expected = GradleException.class)
	public void testNoTestableVariant() {
		def project = ProjectBuilder.builder().build()
		project.pluginManager.apply JenkinsPlugin.class
		project.pluginManager.apply 'com.android.application'
		def android = project.extensions.getByType(AppExtension)
		android.defaultConfig.setApplicationId 'pl.droidsonroids.testapplication'
		android.defaultConfig.setSigningConfig android.signingConfigs.jenkinsRelease
		android.defaultConfig.setMinSdkVersion(1)
		android.buildToolsVersion Constants.BUILD_TOOLS_VERSION
		android.compileSdkVersion Constants.ANDROID_SDK_VERSION
		project.evaluate()
	}

	@Test(expected = GradleException.class)
	public void testNoSigningConfig() {
		def project = ProjectBuilder.builder().build()
		project.pluginManager.apply JenkinsPlugin.class
		project.pluginManager.apply 'com.android.application'
		def android = project.extensions.getByType(AppExtension)
		android.defaultConfig.setApplicationId 'pl.droidsonroids.testapplication'
		android.defaultConfig.setMinSdkVersion(1)
		android.buildToolsVersion Constants.BUILD_TOOLS_VERSION
		android.compileSdkVersion Constants.ANDROID_SDK_VERSION
		android.buildTypes {
			release {
				jenkinsTestable true
			}
		}
		project.evaluate()
	}

}
