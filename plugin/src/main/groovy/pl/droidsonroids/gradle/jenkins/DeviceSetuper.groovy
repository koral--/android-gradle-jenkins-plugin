package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import com.android.utils.StdLogger

import java.util.concurrent.TimeUnit;

public class DeviceSetuper {
	private static final int ADB_COMMAND_TIMEOUT_MILLIS = 30_000
	private static final String MEDIA_SCAN_COMMAND = 'am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://'

	private IShellOutputReceiver outputReceiver = new LoggerBasedOutputReceiver(new StdLogger(StdLogger.Level.VERBOSE))
	private File tempDir

	public DeviceSetuper() {
		tempDir = File.createTempDir()
	}

	public void setup(IDevice device) {
			device.root()
			executeRemoteCommand(device, "settings put global window_animation_scale 0")
			executeRemoteCommand(device, "settings put global transition_animation_scale 0")
			executeRemoteCommand(device, "settings put global animator_duration_scale 0")

		for (name in ['image_portrait.jpg', 'image_square.jpg', 'video.mp4']) {
			def file = pushFile(device, name, '/sdcard/')
			executeRemoteCommand(device, "$MEDIA_SCAN_COMMAND$file")
		}
		executeRemoteCommand(device, "adb shell pm disable com.android.browser")
		if (device.version.featureLevel >= 24) {
			executeRemoteCommand(device, "adb shell pm hide org.chromium.webview_shell")
		}
	}

	private String pushFile(IDevice device, String fileName, String remotePath) {
		def file = new File(tempDir, fileName)
		if (!file.isFile()) {
			file << getClass().getResourceAsStream(fileName)
			file.deleteOnExit()
		}

		def remoteFilePath = remotePath + fileName
		device.pushFile(file.path, remoteFilePath)
		return remoteFilePath
	}

	private executeRemoteCommand(IDevice device, String remoteCommand) {
		device.executeShellCommand(remoteCommand, outputReceiver, ADB_COMMAND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
	}
}