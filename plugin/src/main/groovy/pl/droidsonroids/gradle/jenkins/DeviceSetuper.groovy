package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice

class DeviceSetuper extends DeviceActionPerformer {

	private final File tempDir

	DeviceSetuper() {
		this.tempDir = File.createTempDir()
		Runtime.addShutdownHook {
			tempDir.deleteDir()
		}
	}

	@Override
	void performAction(IDevice device) {
		if (device.version.isGreaterOrEqualThan(17)) {
			executeRemoteCommand(device, 'settings put global window_animation_scale 0')
			executeRemoteCommand(device, 'settings put global transition_animation_scale 0')
			executeRemoteCommand(device, 'settings put global animator_duration_scale 0')
		} else {
			logger.verbose('Animation disabling skipped on %s, API level %d < 17', device.name, device.version.apiLevel)
		}

		for (name in ['image_portrait.jpg', 'image_square.jpg', 'video.mp4']) {
			def file = pushFile(device, name, '/sdcard/')
			executeRemoteCommand(device, "$Constants.MEDIA_SCAN_COMMAND$file")
		}
		executeRemoteCommand(device, 'su 0 pm disable com.android.browser')
		executeRemoteCommand(device, 'su 0 pm hide org.chromium.webview_shell')
		executeRemoteCommand(device, 'su 0 pm hide com.android.chrome')

		if (device.version.isGreaterOrEqualThan(23)) {
			executeRemoteCommand(device, 'wm dismiss-keyguard')
		}
		executeRemoteCommand(device, 'input keyevent 82')
		executeRemoteCommand(device, 'input text 1234')
		executeRemoteCommand(device, 'input keyevent 66')
	}

	String pushFile(IDevice device, String fileName, String remotePath) {
		def file = new File(tempDir, fileName)
		if (!file.isFile()) {
			file << getClass().getResourceAsStream(fileName)
			file.deleteOnExit()
		}

		def remoteFilePath = remotePath + fileName
		device.pushFile(file.path, remoteFilePath)
		return remoteFilePath
	}

}