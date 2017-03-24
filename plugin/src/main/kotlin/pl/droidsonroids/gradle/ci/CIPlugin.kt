package pl.droidsonroids.gradle.ci

import com.android.build.gradle.*
import com.android.ddmlib.DdmPreferences
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.util.GradleVersion

class CIPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val minimumSupportedGradleVersion = GradleVersion.version("3.4")
        if (GradleVersion.current() < minimumSupportedGradleVersion) {
            throw GradleException("Gradle version ${GradleVersion.current()} is not supported. Use Gradle Wrapper or Gradle version >= $minimumSupportedGradleVersion")
        }

        DdmPreferences.setTimeOut(Constants.ADB_COMMAND_TIMEOUT_MILLIS)
        project.addXlintOptionToJavacTasks()

        project.allprojects { subproject ->
            subproject.pluginManager.apply(BasePlugin::class.java)
            subproject.extensions.create("monkeyTest", MonkeyTestExtension::class.java)
            val disablePredex = subproject.hasProperty(Constants.DISABLE_PREDEX_PROPERTY_NAME)
            subproject.plugins.withType(AppPlugin::class.java) {
                val android = subproject.getAndroidExtension<AppExtension>()

                subproject.configureUiTests(android)

                android.setDexOptions(disablePredex)
                android.addJenkinsReleaseBuildType()
                subproject.afterEvaluate {
                    subproject.addMonkeyTask()
                }
            }
            subproject.plugins.withType(LibraryPlugin::class.java) {
                subproject.getAndroidExtension<LibraryExtension>().setDexOptions(disablePredex)
            }
            subproject.plugins.withType(TestPlugin::class.java) {
                subproject.getAndroidExtension<TestExtension>().setDexOptions(disablePredex)
            }
            subproject.addCleanMonkeyOutputTask()
        }
    }

}