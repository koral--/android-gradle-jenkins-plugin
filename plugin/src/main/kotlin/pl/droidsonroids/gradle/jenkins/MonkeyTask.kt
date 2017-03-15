package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.LoggerWrapper
import com.android.builder.testing.ConnectedDeviceProvider
import com.android.builder.testing.api.DeviceProvider
import com.android.ddmlib.ShellCommandUnresponsiveException
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import pl.droidsonroids.gradle.jenkins.Constants.ADB_COMMAND_TIMEOUT_MILLIS
import pl.droidsonroids.gradle.jenkins.Constants.MONKEY_RUN_TIMEOUT_MILLIS
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS

open class MonkeyTask : DefaultTask() {

    @Input
    lateinit var testableVariants: Set<ApplicationVariant>
    @Internal
    lateinit var connectedDeviceProvider: DeviceProvider

    init {
        group = "verification"
        description = "Runs monkey application exerciser on all connected devices and/or emulators"
    }

    @Input
    fun appExtension(android: AppExtension) {
        connectedDeviceProvider = ConnectedDeviceProvider(android.adbExecutable, ADB_COMMAND_TIMEOUT_MILLIS, LoggerWrapper(logger))
    }

    @TaskAction
    fun connectedMonkeyTest() {
        if (testableVariants.isEmpty()) {
            throw GradleException("No monkey testable application variants found")
        }
        connectedDeviceProvider.init()
        val monkeyFile = project.rootProject.file("monkey.txt")
        val executor = Executors.newScheduledThreadPool(1)

        testableVariants.forEach { variant ->
            connectedDeviceProvider.devices.filter {
                it.apiLevel >= variant.mergedFlavor.minSdkVersion.apiLevel
            }.forEach { device ->
                try {
                    val logcatReceiver = MonkeyOutputReceiver(project.logCatFile(device))
                    Thread { device.executeShellCommand("logcat -v time", logcatReceiver, 0, MILLISECONDS) }.start()

                    logger.lifecycle("Monkeying on ${device.name}")

                    val monkeyOutputReceiver = MonkeyOutputReceiver(monkeyFile)
                    val future = executor.schedule({ monkeyOutputReceiver.cancel() }, MONKEY_RUN_TIMEOUT_MILLIS, MILLISECONDS)
                    device.executeShellCommand(variant.monkeyCommand, monkeyOutputReceiver, MONKEY_RUN_TIMEOUT_MILLIS, MILLISECONDS)

                    if (monkeyOutputReceiver.isCancelled) {
                        logger.warn("Monkeying timed out, see monkey.txt and ${device.logcatFileName} for details")
                    }

                    future.cancel(false)
                    logcatReceiver.cancel()
                } catch (e: ShellCommandUnresponsiveException) {
                    logger.log(LogLevel.ERROR, "Monkey timeout on device ${device.name}", e)
                    throw e
                }
            }
        }
        connectedDeviceProvider.terminate()
    }
}