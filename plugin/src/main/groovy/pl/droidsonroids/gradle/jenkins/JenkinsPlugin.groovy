package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.*
import com.android.ddmlib.DdmPreferences
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.util.GradleVersion

public class JenkinsPlugin implements Plugin<Project> {

	static final int ADB_COMMAND_TIMEOUT_MILLIS = 180_000
	static final String UI_TEST_PROPERTY_NAME = 'pl.droidsonroids.jenkins.ui.test'
	private static final String DISABLE_PREDEX_PROPERTY_NAME = 'pl.droidsonroids.jenkins.disablepredex'

	@Override
	void apply(Project project) {
		if (GradleVersion.current() < GradleVersion.version('2.14.1')) {
			throw new GradleException("Gradle version ${GradleVersion.current()} not supported. Use Gradle Wrapper or Gradle version >= 2.6")
		}

		DdmPreferences.setTimeOut(ADB_COMMAND_TIMEOUT_MILLIS)
		Utils.addJavacXlint(project)
		project.allprojects { Project subproject ->
			subproject.pluginManager.apply(BasePlugin)
			TestableExtension jenkinsTestable = subproject.extensions.create('jenkinsTestable', TestableExtension)

			boolean disablePredex = project.hasProperty(DISABLE_PREDEX_PROPERTY_NAME)
			subproject.plugins.withType(AppPlugin) {
				def android = subproject.extensions.getByType(AppExtension)

				if (subproject.hasProperty(UI_TEST_PROPERTY_NAME)) {
					android.sourceSets.getByName('androidTest').setRoot(subproject.rootProject.file('uiTest').path)
					android.applicationVariants.all {
						it.mergedFlavor.setTestInstrumentationRunner jenkinsTestable.testInstrumentationRunner
						subproject.logger.quiet("Instrumentation test runner for ${it.mergedFlavor.name}: $jenkinsTestable.testInstrumentationRunner")
					}
				}

				subproject.tasks.create('connectedSetupUiTests', DeviceSetupTask, {
					appExtension android
				})

				Utils.setDexOptions(android, disablePredex)
				Utils.addJenkinsReleaseBuildType(android)
				subproject.afterEvaluate {
					MonkeyUtils.addMonkeyTask(subproject, android)
				}
			}
			subproject.plugins.withType(LibraryPlugin) {
				Utils.setDexOptions(subproject.extensions.getByType(LibraryExtension), disablePredex)
			}
			subproject.plugins.withType(TestPlugin) {
				Utils.setDexOptions(subproject.extensions.getByType(TestExtension), disablePredex)
			}
			MonkeyUtils.addCleanMonkeyOutputTask(subproject)
		}
	}
}