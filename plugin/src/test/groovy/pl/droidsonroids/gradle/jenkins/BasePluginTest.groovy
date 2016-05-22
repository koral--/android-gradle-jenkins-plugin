package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before

abstract class BasePluginTest {
	Project project
	AppExtension android

	@Before
	void setUp() {
		project = ProjectBuilder.builder().build()
		project.pluginManager.apply JenkinsPlugin.class
		project.pluginManager.apply 'com.android.application'
		android = project.extensions.getByType(AppExtension)
		android.defaultConfig.setApplicationId 'pl.droidsonroids.testapplication'
		android.defaultConfig.setSigningConfig android.signingConfigs.jenkinsRelease
		android.defaultConfig.setMinSdkVersion(1)
		android.buildToolsVersion '23.0.3'
		android.compileSdkVersion 23
	}
}
