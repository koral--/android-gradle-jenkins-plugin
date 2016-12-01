package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

public class DeviceSetupTask extends DefaultTask {
	@Internal
	private AndroidDebugBridge bridge

	public DeviceSetupTask() {
		group = 'verification'
		description = 'Setups device before instrumentation tests'
		AndroidDebugBridge.initIfNeeded(false)
	}

	@Input
	public void appExtension(AppExtension android) {
		bridge = AndroidDebugBridge.createBridge(android.adbExecutable.path, false)
		if (bridge == null) {
			throw new GradleException('Could not create AndroidDebugBridge')
		}
	}

	@TaskAction
	public void setup() {
		if (bridge.devices.length == 0) {
			throw new GradleException('No connected devices')
		}

		DeviceSetuper setuper = new DeviceSetuper()
		bridge.devices.each {
			project.logger.info("Setupping {}", it.name)
			setuper.setup(it)
		}
	}
}