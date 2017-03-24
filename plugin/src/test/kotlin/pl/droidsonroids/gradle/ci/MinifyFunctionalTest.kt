package pl.droidsonroids.gradle.ci

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.gradle.ci.Constants.UI_TEST_MODE_PROPERTY_NAME

class MinifyFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryProjectFolder()

    @Test
    fun `minifyEnabled overridden in noMinify mode`() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("buildType.gradle", "build.gradle")
        temporaryFolder.projectFile("build.gradle").appendText("\nandroid.buildTypes.debug.minifyEnabled true")
        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments("-P$UI_TEST_MODE_PROPERTY_NAME=${UiTestMode.noMinify.name}")
                .withPluginClasspath()
                .withJaCoCo()
                .build()
        assertThat(result.output).contains("minifyEnabled for debug set to false")
    }
}