package pl.droidsonroids.gradle.ci

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

class ConnectedUiTestFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryProjectFolder()

    @Test
    fun `build fails when no connected devices`() {
        temporaryFolder.copyResource("build.gradle", "build.gradle")
        temporaryFolder.newFolder("src", "main")
        temporaryFolder.copyResource("AndroidManifest.xml", "src/main/AndroidManifest.xml")

        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments(Constants.CONNECTED_UI_TEST_TASK_NAME)
                .withPluginClasspath()
                .withJaCoCo()
                .buildAndFail()

        assertThat(result.output).contains("No connected devices")
    }

}