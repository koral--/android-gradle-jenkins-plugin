package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.utils.StdLogger

import static java.util.concurrent.TimeUnit.SECONDS

public class Unlocker implements AndroidDebugBridge.IDeviceChangeListener {

    public static final String ANIMATIONS_SQL_FILE_REMOTE_PATH = '/data/local/tmp/animations.sql'
    def outputReceiver = new StdOutputReceiver(new StdLogger(StdLogger.Level.VERBOSE))
    def bridge
    String animationsSqlFilePath

    public static void main(String[] args) throws Exception {
        new Unlocker().unlock()
    }

    Unlocker() {
        def animationsSqlFile = File.createTempFile('animations', 'sql')
        animationsSqlFile.deleteOnExit()
        animationsSqlFile << getClass().getResourceAsStream('animations.sql')
        animationsSqlFilePath = animationsSqlFile.absolutePath

        AndroidDebugBridge.initIfNeeded(false)
        def adbLocation = new File(System.getenv('ANDROID_HOME'), 'platform-tools/adb')
        bridge = AndroidDebugBridge.createBridge(adbLocation.absolutePath, false)
    }

    def unlock() {
        bridge.addDeviceChangeListener(this)
        unlockAlreadyConnectedDevices()
        waitForever()
    }

    def unlockAlreadyConnectedDevices() {
        bridge.getDevices().each { IDevice device ->
            unlock(device)
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

    def unlock(IDevice device) {
        if (!device.isOnline()) {
            return
        }
        device.pushFile(animationsSqlFilePath, ANIMATIONS_SQL_FILE_REMOTE_PATH)
        device.executeShellCommand("sqlite3 /data/data/com.android.providers.settings/databases/settings.db < ${ANIMATIONS_SQL_FILE_REMOTE_PATH}", outputReceiver, Cleaner.ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
        device.executeShellCommand('input keyevent 82', outputReceiver, 10, SECONDS)
        device.executeShellCommand('input keyevent 4', outputReceiver, 10, SECONDS)
    }

    @Override
    void deviceConnected(IDevice device) {
        unlock(device)
    }

    @Override
    void deviceDisconnected(IDevice device) {
    }

    @Override
    void deviceChanged(IDevice device, int changeMask) {
        if (changeMask == IDevice.CHANGE_STATE) {
            unlock(device)
        }
    }
}