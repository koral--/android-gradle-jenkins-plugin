package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor
import com.android.ddmlib.DdmPreferences
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.util.GradleVersion

public class JenkinsPlugin implements Plugin<Project> {

	static final int ADB_COMMAND_TIMEOUT_MILLIS = 180 * 1000
	private static final String DISABLE_PREDEX_PROPERTY_NAME = 'pl.droidsonroids.jenkins.disablepredex'

	@Override
	void apply(Project project) {
		if (GradleVersion.current() < GradleVersion.version('2.6')) {
			throw new GradleException("Gradle version ${GradleVersion.current()} not supported. Use Gradle Wrapper or Gradle version >= 2.6")
		}

		project.pluginManager.apply(BasePlugin)
		DdmPreferences.setTimeOut(ADB_COMMAND_TIMEOUT_MILLIS)
		addJenkinsTestableDSL()
		addJavacXlint(project)

		project.allprojects { Project subproject ->
			boolean disablePredex = project.hasProperty(DISABLE_PREDEX_PROPERTY_NAME)
			subproject.plugins.withType(AppPlugin) {
				def android = subproject.extensions.getByType(AppExtension)
				if (disablePredex) {
					android.dexOptions.setPreDexLibraries(false)
				}
				addJenkinsReleaseBuildType(android)
				subproject.afterEvaluate {
					addMonkeyTask(subproject, android)
				}
			}
			subproject.plugins.withType(LibraryPlugin) {
				def android = project.extensions.getByType(LibraryExtension)
				if (disablePredex) {
					android.dexOptions.setPreDexLibraries(false)
				}
			}
		}
		addCleanMonkeyOutputTask(project)
	}

	def addCleanMonkeyOutputTask(Project project) {
		def cleanMonkeyOutput = project.tasks.create('cleanMonkeyOutput', Delete)
		cleanMonkeyOutput.delete project.rootProject.fileTree(dir: project.rootDir, includes: ['monkey*'])
		project.clean.dependsOn cleanMonkeyOutput
	}

	static def addJenkinsTestableDSL() {
		DefaultProductFlavor.metaClass.isJenkinsTestable = null
		DefaultBuildType.metaClass.isJenkinsTestable = null
		ProductFlavor.metaClass.jenkinsTestable { boolean isJenkinsTestable ->
			delegate.isJenkinsTestable = isJenkinsTestable
		}
		BuildType.metaClass.jenkinsTestable { boolean isJenkinsTestable ->
			delegate.isJenkinsTestable = isJenkinsTestable
		}
	}

	def addMonkeyTask(Project project, AppExtension android) {
		def applicationVariants = android.applicationVariants.findAll {
			if (it.buildType.isJenkinsTestable != null) {
				return it.buildType.isJenkinsTestable
			}
			for (ProductFlavor flavor : it.productFlavors) {
				if (flavor.isJenkinsTestable != null) {
					return flavor.isJenkinsTestable
				}
			}
			false
		}
		if (applicationVariants.isEmpty()) {
			throw new GradleException('No jenkins testable application variants found')
		}
		def monkeyTask = project.tasks.create(MonkeyTask.MONKEY_TASK_NAME, MonkeyTask, {
			it.init(applicationVariants)
		})
		applicationVariants.each {
			if (it.install == null) {
				throw new GradleException("Variant ${it.name} is marked testable but it is not installable. Missing singningConfig?")
			}
			monkeyTask.dependsOn it.install
		}
	}

	def addJenkinsReleaseBuildType(AppExtension android) {
		android.signingConfigs {
			jenkinsRelease {
				storeFile new File("$System.env.HOME/.android/debug.keystore")
				storePassword 'android'
				keyAlias 'androiddebugkey'
				keyPassword 'android'
			}
		}
	}

	def addJavacXlint(Project project) {
		project.allprojects { Project subproject ->
			gradle.projectsEvaluated {
				subproject.tasks.withType(JavaCompile) {
					options.compilerArgs << '-Xlint'
				}
			}
		}
	}
}