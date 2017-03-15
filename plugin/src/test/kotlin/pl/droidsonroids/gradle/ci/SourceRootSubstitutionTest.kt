package pl.droidsonroids.gradle.ci

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.AndroidSourceSet
import org.assertj.core.api.JUnitSoftAssertions
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test

class SourceRootSubstitutionTest {

    @get:Rule
    val softly = JUnitSoftAssertions()

    @Test
    fun `android source root substituted`() {
        val project = ProjectBuilder.builder().build()
        project.extensions.extraProperties[Constants.UI_TEST_MODE_PROPERTY_NAME] = UiTestMode.minify.name

        project.pluginManager.apply("pl.droidsonroids.jenkins")
        project.pluginManager.apply("com.android.application")

        project.extensions.configure(AppExtension::class.java, {
            val jenkinsRelease = it.signingConfigs.getByName("jenkinsRelease")
            it.defaultConfig {
                it.applicationId = "pl.droidsonroids.testapplication"
                it.signingConfig = jenkinsRelease
                it.minSdkVersion(1)
            }
            it.buildToolsVersion = "25.0.2"
            it.compileSdkVersion(25)
        })
        val androidTest: AndroidSourceSet = project.getAndroidExtension<AppExtension>().sourceSets.getByName("androidTest")

        val baseDir = project.rootProject.file("uiTest").path

        with(androidTest) {
            val sets = arrayOf(java.srcDirs, resources.srcDirs, res.srcDirs, aidl.srcDirs,
                    renderscript.srcDirs, jni.srcDirs, jniLibs.srcDirs, shaders.srcDirs)
            sets.forEach {
                softly.assertThat(it).hasSize(1)
                softly.assertThat(it.first().path).startsWith(baseDir)
            }
            softly.assertThat(manifest.srcFile.path).startsWith(baseDir)
        }
    }
}