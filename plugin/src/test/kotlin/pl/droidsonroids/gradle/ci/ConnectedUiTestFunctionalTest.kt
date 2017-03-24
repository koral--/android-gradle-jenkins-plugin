package pl.droidsonroids.gradle.ci

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import java.io.File

fun GradleRunner.withJacoco(): GradleRunner {
    javaClass.classLoader.getResourceAsStream("testkit-gradle.properties").toFile(File(projectDir, "gradle.properties"))
    return this
}

class ConnectedUiTestFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryProjectFolder()

    @Test
    fun `build fails when no connected devices`() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("noTestableVariant.gradle", "build.gradle")
        temporaryFolder.newFolder("src", "main")
        temporaryFolder.copyResource("AndroidManifest.xml", "src/main/AndroidManifest.xml")

        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments(Constants.CONNECTED_UI_TEST_TASK_NAME, "-P${Constants.UI_TEST_MODE_PROPERTY_NAME}=${UiTestMode.noMinify.name}")
                .withPluginClasspath()
                .withJacoco()
                .buildAndFail()

        assertThat(result.output).contains("No connected devices")
    }

}