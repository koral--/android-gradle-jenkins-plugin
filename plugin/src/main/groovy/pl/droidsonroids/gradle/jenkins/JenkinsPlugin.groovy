package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.*
import com.android.ddmlib.DdmPreferences
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.util.GradleVersion

class JenkinsPlugin implements Plugin<Project> {


	@Override
	void apply(Project project) {
		if (GradleVersion.current() < GradleVersion.version('2.14.1')) {
			throw new GradleException("Gradle version ${GradleVersion.current()} not supported. Use Gradle Wrapper or Gradle version >= 2.14.1")
		}

		DdmPreferences.setTimeOut(Constants.ADB_COMMAND_TIMEOUT_MILLIS)
		Utils.addJavacXlint(project)

		project.allprojects { Project subproject ->
			subproject.pluginManager.apply(BasePlugin)
			MonkeyTestExtension monkeyTest = subproject.extensions.create('monkeyTest', MonkeyTestExtension)
			UiTestExtension uiTest = subproject.extensions.create('uiTest', UiTestExtension)

			boolean disablePredex = project.hasProperty(Constants.DISABLE_PREDEX_PROPERTY_NAME)
			subproject.plugins.withType(AppPlugin) {
				def android = subproject.extensions.getByType(AppExtension)

				UiTestUtils.configureUiTests(android, subproject, uiTest)

				Utils.setDexOptions(android, disablePredex)
				Utils.addJenkinsReleaseBuildType(android)
				subproject.afterEvaluate {
					MonkeyUtils.addMonkeyTask(subproject, android, monkeyTest)
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