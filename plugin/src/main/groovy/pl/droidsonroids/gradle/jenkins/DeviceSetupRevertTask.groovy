package pl.droidsonroids.gradle.jenkins;

public class DeviceSetupRevertTask extends DeviceActionTask {

	public DeviceSetupRevertTask() {
		description = 'Reverts device setup after instrumentation tests'
	}

	protected DeviceSetuper getActionPerformer() {
		new DeviceSetupReverter()
	}
}