package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project

static void addUITestsConfiguration(AppExtension android, Project subproject, TestableExtension jenkinsTestable) {
	def uiTestModeName = subproject.findProperty(Constants.UI_TEST_MODE_PROPERTY_NAME)
	if (uiTestModeName == null) {
		return
	}
	def uiTestMode = UiTestMode.valueOf(uiTestModeName)

	android.sourceSets.getByName('androidTest').setRoot(subproject.rootProject.file('uiTest').path)
	DomainObjectSet<ApplicationVariant> variants = android.applicationVariants

	variants.all {
		if (it.buildType.name == android.testBuildType) {
			def defaultMinifyEnabled = jenkinsTestable.getDefaultMinifyEnabled(variants)
			def minifyEnabled = uiTestMode.getMinifyEnabled(defaultMinifyEnabled)
			android.buildTypes."$android.testBuildType".minifyEnabled minifyEnabled
			subproject.logger.quiet("minifyEnabled for $it.buildType.name set to $minifyEnabled")
		}
		it.mergedFlavor.setTestInstrumentationRunner jenkinsTestable.testInstrumentationRunner
		subproject.logger.quiet("Instrumentation test runner for ${it.mergedFlavor.name}: $jenkinsTestable.testInstrumentationRunner")
	}
}
