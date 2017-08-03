package pl.droidsonroids.gradle.ci

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.SPOON_TASK_NAME
import java.io.File
import java.io.InputStream

inline fun <reified T : BaseExtension> Project.getAndroidExtension(): T =
        extensions.getByType(T::class.java)

fun BaseExtension.setDexOptions(disablePredex: Boolean) {
    if (disablePredex) {
        dexOptions.preDexLibraries = false
    }
}

fun Project.configureUiTests(android: AppExtension) {
    val deviceSetupTask = tasks.create(CONNECTED_SETUP_UI_TEST_TASK_NAME, DeviceSetupTask::class.java, {
        it.appExtension(android)
    })

    val deviceSetupRevertTask = tasks.create(CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME, DeviceSetupRevertTask::class.java, {
        it.appExtension(android)
    })

    afterEvaluate {
        val isSplitEnabled = android.splits.let { it.abi.isEnable || it.density.isEnable || it.language.isEnable }

        val instrumentationTestTask = when {
            isSplitEnabled -> tasks.getByName("connectedCheck")
            else -> {
                apply(mapOf("plugin" to "spoon"))
                tasks.getByName(SPOON_TASK_NAME)
            }
        }

        instrumentationTestTask.mustRunAfter(deviceSetupTask)

        tasks.create(CONNECTED_UI_TEST_TASK_NAME) {
            it.group = "verification"
            it.description = "Setups connected devices and performs instrumentation tests"
            it.dependsOn(instrumentationTestTask, deviceSetupTask)
            it.finalizedBy(deviceSetupRevertTask)
        }
    }
}

fun InputStream.toFile(file: File) {
    use { input ->
        file.outputStream().use { input.copyTo(it) }
    }
}
