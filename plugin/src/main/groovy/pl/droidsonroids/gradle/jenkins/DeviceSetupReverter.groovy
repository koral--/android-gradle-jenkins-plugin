package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice

public class DeviceSetupReverter extends DeviceActionPerformer {

	public DeviceSetupReverter() {
	}

	@Override
	public void performAction(IDevice device) {
		if (device.version.isGreaterOrEqualThan(17)) {
			executeRemoteCommand(device, 'settings put global window_animation_scale 1')
			executeRemoteCommand(device, 'settings put global transition_animation_scale 1')
			executeRemoteCommand(device, 'settings put global animator_duration_scale 1')
		}
		executeRemoteCommand(device, 'su 0 pm enable com.android.browser')
		executeRemoteCommand(device, 'su 0 pm unhide org.chromium.webview_shell')
		executeRemoteCommand(device, 'su 0 pm unhide com.android.chrome')
		executeRemoteCommand(device, 'input keyevent 26')
	}

}