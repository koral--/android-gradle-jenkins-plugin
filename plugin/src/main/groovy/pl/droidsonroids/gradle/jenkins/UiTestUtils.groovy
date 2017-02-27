package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project

import static pl.droidsonroids.gradle.jenkins.Constants.*

static void configureUiTests(AppExtension android, Project subproject, UiTestExtension uiTest) {
	String uiTestModeName = subproject.findProperty(UI_TEST_MODE_PROPERTY_NAME)
	if (uiTestModeName == null) {
		return
	}

	def deviceSetupTask = subproject.tasks.create(CONNECTED_SETUP_UI_TEST_TASK_NAME, DeviceSetupTask, {
		it.appExtension android
	})

	def deviceSetupRevertTask = subproject.tasks.create(CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME, DeviceSetupRevertTask, {
		it.appExtension android
	})

	def uninstallAllTask = subproject.tasks.findByName(UNINSTALL_ALL_TASK_NAME)

	subproject.apply plugin: 'spoon'

	def spoonTask = subproject.tasks.getByName(SPOON_TASK_NAME)
	spoonTask.mustRunAfter deviceSetupTask, uninstallAllTask

	subproject.tasks.create(CONNECTED_UI_TEST_TASK_NAME) {
		it.group = 'verification'
		it.description = 'Setups connected devices and performs instrumentation tests'
		it.dependsOn spoonTask, deviceSetupTask, uninstallAllTask
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
