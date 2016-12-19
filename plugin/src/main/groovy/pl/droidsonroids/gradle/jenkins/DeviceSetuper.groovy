package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice

public class DeviceSetuper extends DeviceActionPerformer {

	private final File tempDir

	public DeviceSetuper() {
		this.tempDir = File.createTempDir()
		Runtime.addShutdownHook {
			tempDir.deleteDir()
		}
	}

	@Override
	public void performAction(IDevice device) {
		executeRemoteCommand(device, 'settings put global window_animation_scale 0')
		executeRemoteCommand(device, 'settings put global transition_animation_scale 0')
		executeRemoteCommand(device, 'settings put global animator_duration_scale 0')

		for (name in ['image_portrait.jpg', 'image_square.jpg', 'video.mp4']) {
			def file = pushFile(device, name, '/sdcard/')
			executeRemoteCommand(device, "$Constants.MEDIA_SCAN_COMMAND$file")
		}
		executeRemoteCommand(device, 'su 0 pm disable com.android.browser')
		executeRemoteCommand(device, 'su 0 pm hide org.chromium.webview_shell')
		executeRemoteCommand(device, 'su 0 pm hide com.android.chrome')
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