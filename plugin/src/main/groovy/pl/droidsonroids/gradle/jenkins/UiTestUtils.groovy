package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import org.gradle.api.Project

static void addUITestsConfiguration(AppExtension android, Project subproject, TestableExtension jenkinsTestable) {

	def uiTestModeName = subproject.findProperty(JenkinsPlugin.UI_TEST_MODE_PROPERTY_NAME)
	if (uiTestModeName != null) {
		android.sourceSets.getByName('androidTest').setRoot(subproject.rootProject.file('uiTest').path)
		android.applicationVariants.all {
			it.mergedFlavor.setTestInstrumentationRunner jenkinsTestable.testInstrumentationRunner
			subproject.logger.quiet("Instrumentation test runner for ${it.mergedFlavor.name}: $jenkinsTestable.testInstrumentationRunner")
		}
	}
}