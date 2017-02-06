package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice

class DeviceSetupReverter extends DeviceActionPerformer {

	DeviceSetupReverter() {
	}

	@Override
	void performAction(IDevice device) {
		if (device.version.isGreaterOrEqualThan(17)) {
			executeRemoteCommand(device, 'settings put global window_animation_scale 1')
			executeRemoteCommand(device, 'settings put global transition_animation_scale 1')
			executeRemoteCommand(device, 'settings put global animator_duration_scale 1')
		}
		executeRemoteCommand(device, 'su 0 pm enable com.android.browser')
		executeRemoteCommand(device, 'su 0 pm unhide org.chromium.webview_shell')
		executeRemoteCommand(device, 'su 0 pm unhide com.android.chrome')
	}

}