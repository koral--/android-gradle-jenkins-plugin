package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.LoggerWrapper
import com.android.builder.testing.ConnectedDeviceProvider
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.util.concurrent.TimeUnit

class MonkeyTask extends DefaultTask {

    Project subproject
    Set<ApplicationVariant> applicationVariants

    {
        group = 'verification'
        description = 'Runs monkey application exerciser on all connected devices and/or emulators'
    }

    @TaskAction
    def connectedMonkeyTest() {
        def adbExe = subproject.extensions.getByType(AppExtension).adbExe
        def connectedDeviceProvider = new ConnectedDeviceProvider(adbExe, new LoggerWrapper(subproject.logger))
        connectedDeviceProvider.init()
        applicationVariants.each {
            variant ->
                def command = 'monkey -v -p ' + variant.applicationId + ' 1000'
                connectedDeviceProvider.getDevices().findAll {
                    it.apiLevel >= variant.mergedFlavor.minSdkVersion.apiLevel
                }.each { device ->
                    device.executeShellCommand(command, new MonkeyOutputReceiver(), 5, TimeUnit.SECONDS)
                }
        }
        connectedDeviceProvider.terminate()
    }
}