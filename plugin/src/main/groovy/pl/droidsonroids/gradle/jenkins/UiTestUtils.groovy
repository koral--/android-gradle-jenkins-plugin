package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project

static void addUITestsConfiguration(AppExtension android, Project subproject, UiTestExtension uiTest) {
	String uiTestModeName = subproject.findProperty(Constants.UI_TEST_MODE_PROPERTY_NAME)
	if (uiTestModeName == null) {
		return
	}

	def deviceSetupTask = subproject.tasks.create(Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME, DeviceSetupTask, {
		it.appExtension android
	})

	def deviceSetupRevertTask = subproject.tasks.create(Constants.CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME, DeviceSetupRevertTask, {
		it.appExtension android
	})

	subproject.apply plugin: 'spoon'

	def spoonTask = subproject.tasks.getByName(Constants.SPOON_TASK_NAME)
	spoonTask.mustRunAfter deviceSetupTask

	subproject.tasks.create(Constants.CONNECTED_UI_TEST_TASK_NAME) {
		it.group = 'verification'
		it.description = 'Setups connected devices and performs instrumentation tests'
		it.dependsOn spoonTask, deviceSetupTask
		it.finalizedBy deviceSetupRevertTask
	}

	def uiTestMode = UiTestMode.valueOf(uiTestModeName)

	android.sourceSets.getByName('androidTest').setRoot(subproject.rootProject.file('uiTest').path)
	DomainObjectSet<ApplicationVariant> variants = android.applicationVariants

	variants.all {
		if (it.buildType.name == android.testBuildType) {
			def defaultMinifyEnabled = uiTest.getDefaultMinifyEnabled(variants)
			def minifyEnabled = uiTestMode.getMinifyEnabled(defaultMinifyEnabled)
			android.buildTypes."$android.testBuildType".minifyEnabled minifyEnabled
			subproject.logger.quiet("minifyEnabled for $it.buildType.name set to $minifyEnabled")
		}
		it.mergedFlavor.setTestInstrumentationRunner uiTest.testInstrumentationRunner
		subproject.logger.quiet("Instrumentation test runner for ${it.name}: $uiTest.testInstrumentationRunner")
	}
}
