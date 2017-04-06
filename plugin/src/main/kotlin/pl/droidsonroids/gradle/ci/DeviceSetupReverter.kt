package pl.droidsonroids.gradle.ci

import com.android.ddmlib.IDevice

class DeviceSetupReverter : DeviceWorker() {

    override fun doWork(device: IDevice) {
        if (device.version.isGreaterOrEqualThan(17)) {
            device.executeRemoteCommand("settings put global window_animation_scale 1")
            device.executeRemoteCommand("settings put global transition_animation_scale 1")
            device.executeRemoteCommand("settings put global animator_duration_scale 1")
        }

        device.executeRemoteCommand("su 0 pm enable com.android.browser")
        device.executeRemoteCommand("su 0 pm unhide org.chromium.webview_shell")
        device.executeRemoteCommand("su 0 pm unhide com.android.chrome")
    }
}
