package pl.droidsonroids.gradle.jenkins;

public class DeviceSetupTask extends DeviceActionTask {

	public DeviceSetupTask() {
		description = 'Setups device before instrumentation tests'
	}

	protected DeviceSetuper getActionPerformer() {
		new DeviceSetuper()
	}
}