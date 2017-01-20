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

import java.util.concurrent.Executors

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static pl.droidsonroids.gradle.jenkins.Constants.ADB_COMMAND_TIMEOUT_MILLIS
import static pl.droidsonroids.gradle.jenkins.Constants.MONKEY_RUN_TIMEOUT_MILLIS

class MonkeyTask extends DefaultTask {

	@Internal
	Set<ApplicationVariant> testableVariants
	@Internal
	DeviceProvider connectedDeviceProvider

	public MonkeyTask() {
		group = 'verification'
		description = 'Runs monkey application exerciser on all connected devices and/or emulators'
	}

	@Input
	public appExtension(AppExtension android) {
		connectedDeviceProvider = new ConnectedDeviceProvider(android.adbExecutable, ADB_COMMAND_TIMEOUT_MILLIS, new LoggerWrapper(project.logger))
	}

	@Input
	public testableVariants(Set<ApplicationVariant> testableVariants) {
		this.testableVariants = testableVariants
	}

	@TaskAction
	def connectedMonkeyTest() {
		if (testableVariants.empty) {
			throw new GradleException('No jenkins testable application variants found')
		}
		connectedDeviceProvider.init()
		def monkeyFile = project.rootProject.file('monkey.txt')
		def executor = Executors.newScheduledThreadPool(1)
		testableVariants.each { variant ->
			def command = "monkey -v --ignore-crashes --ignore-timeouts --ignore-security-exceptions --monitor-native-crashes --ignore-native-crashes -p ${variant.applicationId} 1000"
			connectedDeviceProvider.devices.findAll {
				it.apiLevel >= variant.mergedFlavor.minSdkVersion.apiLevel
			}.each { device ->
				try {
					def logcatFileName = "monkey-logcat-${device.name.replace(' ', '_')}.txt"
					def logcatFile = project.rootProject.file(logcatFileName)
					def logcatReceiver = new MonkeyOutputReceiver(logcatFile)
					Thread.start { device.executeShellCommand('logcat -v time', logcatReceiver, 0, MILLISECONDS) }

					project.logger.lifecycle('Monkeying on {}', device.name)
					def monkeyOutputReceiver = new MonkeyOutputReceiver(monkeyFile)
					def future = executor.schedule({ monkeyOutputReceiver.cancel() }, MONKEY_RUN_TIMEOUT_MILLIS, MILLISECONDS)
					device.executeShellCommand(command, monkeyOutputReceiver, MONKEY_RUN_TIMEOUT_MILLIS, MILLISECONDS)

					if (monkeyOutputReceiver.isCancelled()) {
						project.logger.warn("Monkeying timed out, see monkey.txt and $logcatFileName for details")
					}
					future.cancel(false)
					logcatReceiver.cancel()
				} catch (ShellCommandUnresponsiveException ex) {
					project.logger.log(LogLevel.ERROR, "Monkey timeout on device ${device.name}", ex)
					throw ex
				}
			}
		}
		connectedDeviceProvider.terminate()
	}
}