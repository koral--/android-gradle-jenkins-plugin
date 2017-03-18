package pl.droidsonroids.gradle.ci

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class DexOptionsTest {

    private lateinit var project: Project

    @Before
    fun setUp() {
        project = ProjectBuilder.builder().build()
    }

    fun Project.applyPlugin(id: String) = pluginManager.apply(id)

    fun Project.setDisablePredex(value: Boolean) {
        extensions.extraProperties[Constants.DISABLE_PREDEX_PROPERTY_NAME] = value
    }

    @Test
    fun `preDexLibraries disabled in application project when property set`() {
        project.setDisablePredex(true)
        project.applyPlugin("pl.droidsonroids.jenkins")
        project.applyPlugin("com.android.application")

        assertThat(project.getAndroidExtension<AppExtension>().dexOptions.preDexLibraries).isFalse()
    }

    @Test
    fun `preDexLibraries disabled in library project when property set`() {
        project.setDisablePredex(true)
        project.applyPlugin("pl.droidsonroids.jenkins")
        project.applyPlugin("com.android.library")

        assertThat(project.getAndroidExtension<LibraryExtension>().dexOptions.preDexLibraries).isFalse()
    }

    @Test
    fun `preDexLibraries disabled in test project when property set`() {
        project.setDisablePredex(true)
        project.applyPlugin("pl.droidsonroids.jenkins")
        project.applyPlugin("com.android.test")

        assertThat(project.getAndroidExtension<TestExtension>().dexOptions.preDexLibraries).isFalse()
    }

    @Test
    fun `preDexLibraries enabled by default`() {
        project.applyPlugin("pl.droidsonroids.jenkins")
        project.applyPlugin("com.android.application")

        assertThat(project.getAndroidExtension<AppExtension>().dexOptions.preDexLibraries).isTrue()
    }
}