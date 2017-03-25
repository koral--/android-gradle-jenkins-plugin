package pl.droidsonroids.gradle.ci

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.SPOON_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.UI_TEST_MODE_PROPERTY_NAME

class SetupFunctionalTest {
    @get:Rule
    val temporaryFolder = TemporaryProjectFolder()

    @Test
    fun `setup fails when there is no connected devices`() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("buildType.gradle", "build.gradle")
        val result = GradleRunner
                .create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments(CONNECTED_SETUP_UI_TEST_TASK_NAME)
                .withPluginClasspath()
                .withJaCoCo()
                .buildAndFail()
        assertThat(result.output).contains("No connected devices")
    }

    @Test
    fun `setup invoked as dependent task`() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("buildType.gradle", "build.gradle")
        val result = GradleRunner
                .create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments(CONNECTED_UI_TEST_TASK_NAME, "-m")
                .withPluginClasspath()
                .withJaCoCo()
                .build()

        assertThat(result.task(":$CONNECTED_SETUP_UI_TEST_TASK_NAME")).isNotNull()
        assertThat(result.task(":$SPOON_TASK_NAME")).isNotNull()
        assertThat(result.task(":$CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME")).isNotNull()
    }
}
