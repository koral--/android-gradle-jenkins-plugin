package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.utils.StdLogger

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.SECONDS

public class Setuper implements AndroidDebugBridge.IDeviceChangeListener {

	private static final String ANIMATIONS_SQL_FILE_REMOTE_PATH = '/data/local/tmp/'
	private static final String ANIMATOR_DURATION_SCALE = '\'<setting id="67" name="animator_duration_scale" value="0.0" package="android" />\''
	private static final String WINDOW_ANIMATION_SCALE = '\'<setting id="18" name="window_animation_scale" value="0.0" package="android" />\''
	private static final String TRANSITION_ANIMATION_SCALE = '\'<setting id="19" name="transition_animation_scale" value="0.0" package="android" />\''
	private static final String MEDIA_SCAN_COMMAND = 'am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://'

	def outputReceiver = new LoggerBasedOutputReceiver(new StdLogger(StdLogger.Level.VERBOSE))
	def bridge
	private File tempDir

	public static void main(String[] args) throws Exception {
		new Setuper().setup()
	}

	Setuper() {
		AndroidDebugBridge.initIfNeeded(false)
		tempDir = File.createTempDir()
		final androidHomeDir = System.getenv('ANDROID_HOME') ?: '/opt/android-sdk-update-manager'
		def adbLocation = new File(androidHomeDir, 'platform-tools/adb')
		bridge = AndroidDebugBridge.createBridge(adbLocation.absolutePath, false)
	}

	def setup() {
		bridge.addDeviceChangeListener(this)
		unlockAlreadyConnectedDevices()
		waitForever()
	}

	def unlockAlreadyConnectedDevices() {
		bridge.getDevices().each { IDevice device ->
			setup(device)
		}
	}

	@SuppressWarnings("GroovyInfiniteLoopStatement")
	def waitForever() {
		synchronized (this) {
			while (true) {
				wait()
			}
		}
	}

	def setup(IDevice device) {

		if (!device.online) {
			return
		}
		String bootCompleted = null
		int tryCount = 3
		while (tryCount-- > 0 && bootCompleted != '1') {
			bootCompleted = device.getSystemProperty('sys.boot_completed').get(Cleaner.ADB_COMMAND_TIMEOUT_MILLIS, SECONDS)
		}

		if (device.version.featureLevel >= 23) {
			device.root()
			executeRemoteCommand(device, "echo $ANIMATOR_DURATION_SCALE >> /data/system/users/0/settings_global.xml")
			executeRemoteCommand(device, "echo $WINDOW_ANIMATION_SCALE >> /data/system/users/0/settings_system.xml")
			executeRemoteCommand(device, "echo $TRANSITION_ANIMATION_SCALE >> /data/system/users/0/settings_system.xml")
		} else {
			pushFile(device, 'settings.sql', ANIMATIONS_SQL_FILE_REMOTE_PATH)
			executeRemoteCommand(device, "sqlite3 /data/data/com.android.providers.settings/databases/settings.db < ${ANIMATIONS_SQL_FILE_REMOTE_PATH}")
		}

		for (name in ['image_portrait.jpg', 'image_square.jpg', 'video.mp4']) {
			def file = pushFile(device, name, '/sdcard/')
			executeRemoteCommand(device, "$MEDIA_SCAN_COMMAND$file")
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
		device.executeShellCommand(remoteCommand, outputReceiver, Cleaner.ADB_COMMAND_TIMEOUT_MILLIS, MILLISECONDS)
	}

	@Override
	void deviceConnected(IDevice device) {
		setup(device)
	}

	@Override
	void deviceDisconnected(IDevice device) {
	}

	@Override
	void deviceChanged(IDevice device, int changeMask) {
		if (changeMask == IDevice.CHANGE_STATE) {
			setup(device)
		}
	}
}