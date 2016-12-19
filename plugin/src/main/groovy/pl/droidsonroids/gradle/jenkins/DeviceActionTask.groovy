package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class DeviceActionTask extends DefaultTask {

	@Internal
	private AndroidDebugBridge bridge

	protected DeviceActionTask() {
		group = 'verification'
		AndroidDebugBridge.initIfNeeded(false)
	}

	@Input
	public void appExtension(AppExtension android) {
		bridge = AndroidDebugBridge.createBridge(android.adbExecutable.path, false)
		if (bridge == null) {
			throw new GradleException('Could not create AndroidDebugBridge')
		}
	}

	protected abstract DeviceSetuper getActionPerformer()

	@TaskAction
	public void performAction() {
		if (bridge.devices.length == 0) {
			throw new GradleException('No connected devices')
		}

		DeviceActionPerformer performer = getActionPerformer()
		bridge.devices.each {
			project.logger.info("Preparing {}", it.name)
			performer.performAction(it)
		}
	}

}