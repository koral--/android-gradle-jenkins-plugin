package pl.droidsonroids.gradle.jenkins

import org.gradle.api.tasks.Internal

class DeviceSetupTask extends DeviceActionTask {

	DeviceSetupTask() {
		description = 'Setups device before instrumentation tests'
	}

	@Internal
	protected DeviceActionPerformer getActionPerformer() {
		new DeviceSetuper()
	}
}