package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

public class SetupTask extends DefaultTask {
	private AndroidDebugBridge bridge

	public SetupTask() {
		group = 'verification'
		description = 'Setups device before instrumentation tests'
		AndroidDebugBridge.initIfNeeded(false)
	}

	public void init(AppExtension android) {
		def adbExecutable = android.adbExecutable
		bridge = AndroidDebugBridge.createBridge(adbExecutable.path, false)
		if (bridge == null) {
			throw new GradleException('Could not create AndroidDebugBridge')
		}
	}

	@TaskAction
	public void setup() {
		if (bridge.devices.length == 0) {
			throw new GradleException('No connected devices')
		}
		def setuper = new DeviceSetuper()

		bridge.devices.each {
			project.logger.info("Setupping {}", it.name)
			setuper.setup(it)
		}
	}
}