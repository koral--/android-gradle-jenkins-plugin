package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.*
import com.android.builder.model.ProductFlavor
import com.android.ddmlib.DdmPreferences
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Delete
import org.gradle.util.GradleVersion

import static pl.droidsonroids.gradle.jenkins.MonkeyTask.MONKEY_TASK_NAME

public class JenkinsPlugin implements Plugin<Project> {

	static final int ADB_COMMAND_TIMEOUT_MILLIS = 180_000
	private static final String DISABLE_PREDEX_PROPERTY_NAME = 'pl.droidsonroids.jenkins.disablepredex'
	public static final String DEFAULT_MOCK_WEB_SERVER_BASE_URL = 'http://localhost:12345'

	@Override
	void apply(Project project) {
		if (GradleVersion.current() < GradleVersion.version('2.14.1')) {
			throw new GradleException("Gradle version ${GradleVersion.current()} not supported. Use Gradle Wrapper or Gradle version >= 2.6")
		}

		DdmPreferences.setTimeOut(ADB_COMMAND_TIMEOUT_MILLIS)
		Utils.addJavacXlint(project)
		project.allprojects { Project subproject ->
			subproject.pluginManager.apply(BasePlugin)
			subproject.extensions.create('jenkinsTestable', TestableExtension)

			boolean disablePredex = project.hasProperty(DISABLE_PREDEX_PROPERTY_NAME)
			subproject.plugins.withType(AppPlugin) {
				def android = subproject.extensions.getByType(AppExtension)

				if (subproject.hasProperty('pl.droidsonroids.jenkins.uiTest')) {
					android.sourceSets.getByName('androidTest').setRoot(subproject.rootProject.file('uiTest').path)
				}

				if (!subproject.hasProperty('pl.droidsonroids.jenkins.mockWebServerBaseUrl')) {
					subproject.ext.'pl.droidsonroids.jenkins.mockWebServerBaseUrl' = DEFAULT_MOCK_WEB_SERVER_BASE_URL
				}

				subproject.tasks.create('connectedSetupUiTests', SetupTask, {
					appExtension android
				})

				Utils.setDexOptions(android, disablePredex)
				Utils.addJenkinsReleaseBuildType(android)
				subproject.afterEvaluate {
					addMonkeyTask(subproject, android)
				}
			}
			subproject.plugins.withType(LibraryPlugin) {
				Utils.setDexOptions(subproject.extensions.getByType(LibraryExtension), disablePredex)
			}
			subproject.plugins.withType(TestPlugin) {
				Utils.setDexOptions(subproject.extensions.getByType(TestExtension), disablePredex)
			}
			addCleanMonkeyOutputTask(subproject)
		}
	}

	static def addCleanMonkeyOutputTask(Project project) {
		def cleanMonkeyOutput = project.tasks.create('cleanMonkeyOutput', Delete)
		cleanMonkeyOutput.delete project.rootProject.fileTree(dir: project.rootDir, includes: ['monkey-logcat-*.txt'])
		project.clean.dependsOn cleanMonkeyOutput
	}

	static def addMonkeyTask(Project project, AppExtension android) {
		def jenkinsTestable = project.extensions.getByType(TestableExtension)

		def applicationVariants = android.applicationVariants.findAll {
			if (jenkinsTestable.variantNames.contains(it.name)) {
				return true
			}
			if (jenkinsTestable.buildTypeNames.contains(it.buildType.name)) {
				return true
			}
			for (ProductFlavor flavor : it.productFlavors) {
				if (jenkinsTestable.productFlavorNames.contains(flavor.name)) {
					return true
				}
			}
			false
		}

		def monkeyTask = project.tasks.create(MONKEY_TASK_NAME, MonkeyTask, {
			appExtension android
		})
		applicationVariants.each {
			if (it.install == null) {
				throw new GradleException("Variant ${it.name} is marked testable but it is not installable. Missing singningConfig?")
			}
			project.logger.quiet("`$it.name` build variant is testable by monkey")
			monkeyTask.dependsOn it.install
		}
	}
}