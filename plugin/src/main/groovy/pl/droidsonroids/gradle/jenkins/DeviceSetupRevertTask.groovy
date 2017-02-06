package pl.droidsonroids.gradle.jenkins

import org.gradle.api.tasks.Internal

class DeviceSetupRevertTask extends DeviceActionTask {

	DeviceSetupRevertTask() {
		description = 'Reverts device setup after instrumentation tests'
	}

	@Internal
	protected DeviceActionPerformer getActionPerformer() {
		new DeviceSetupReverter()
	}
}