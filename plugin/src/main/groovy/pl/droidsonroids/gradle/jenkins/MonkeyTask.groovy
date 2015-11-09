package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.LoggerWrapper
import com.android.builder.testing.ConnectedDeviceProvider
import com.android.builder.testing.api.DeviceProvider
import com.android.ddmlib.ShellCommandUnresponsiveException
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import java.util.concurrent.TimeUnit

import static pl.droidsonroids.gradle.jenkins.JenkinsPlugin.ADB_COMMAND_TIMEOUT_MILLIS

class MonkeyTask extends DefaultTask {

    protected static final String MONKEY_TASK_NAME = 'connectedMonkeyJenkinsTest'

    Set<ApplicationVariant> applicationVariants
    Logger logger
    DeviceProvider connectedDeviceProvider

    {
        group = 'verification'
        description = 'Runs monkey application exerciser on all connected devices and/or emulators'
    }

    def init(Set<ApplicationVariant> applicationVariants) {
        this.logger = project.logger
        this.applicationVariants = applicationVariants
        def adbExe = project.extensions.getByType(AppExtension).adbExe
        connectedDeviceProvider = new ConnectedDeviceProvider(adbExe, new LoggerWrapper(logger))
    }

    @TaskAction
    def connectedMonkeyTest() {
        connectedDeviceProvider.init()
        def monkeyFile = project.rootProject.file('monkey.txt')
        applicationVariants.each { variant ->
            def command = 'monkey -v --ignore-crashes --ignore-timeouts --ignore-security-exceptions --monitor-native-crashes --ignore-native-crashes -p ' + variant.applicationId + ' 1000'
            connectedDeviceProvider.getDevices().findAll {
                it.apiLevel >= variant.mergedFlavor.minSdkVersion.apiLevel
            }.each { device ->
                try {
                    def logcatFile = project.rootProject.file("monkey-logcat-${device.name}.txt")
                    def logcatReceiver = new MonkeyOutputReceiver(logcatFile)
                    Thread.start {
                        device.executeShellCommand('logcat -v time', logcatReceiver, 0, TimeUnit.SECONDS)
                    }

                    logger.lifecycle('Monkeying on {}', device.name)

                    device.executeShellCommand(command, new MonkeyOutputReceiver(monkeyFile), ADB_COMMAND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    logcatReceiver.cancel()
                } catch (ShellCommandUnresponsiveException ex) {
                    logger.log(LogLevel.ERROR, 'Monkey timeout on device ' + device.name, ex)
                    throw ex
                }
            }
        }
        connectedDeviceProvider.terminate()
    }
}