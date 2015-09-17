package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.utils.StdLogger

import static java.util.concurrent.TimeUnit.SECONDS

public class Setuper implements AndroidDebugBridge.IDeviceChangeListener {

    public static final String ANIMATIONS_SQL_FILE_REMOTE_PATH = '/data/local/tmp/animations.sql'
    def outputReceiver = new LoggerBasedOutputReceiver(new StdLogger(StdLogger.Level.VERBOSE))
    def bridge
    String animationsSqlFilePath

    public static void main(String[] args) throws Exception {
        new Setuper().setup()
    }

    Setuper() {
        def animationsSqlFile = File.createTempFile('animations', 'sql')
        animationsSqlFile.deleteOnExit()
        animationsSqlFile << getClass().getResourceAsStream('animations.sql')
        animationsSqlFilePath = animationsSqlFile.absolutePath

        AndroidDebugBridge.initIfNeeded(false)
        def adbLocation = new File(System.getenv('ANDROID_HOME'), 'platform-tools/adb')
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
        if (!device.isOnline()) {
            return
        }
        device.pushFile(animationsSqlFilePath, ANIMATIONS_SQL_FILE_REMOTE_PATH)

        final remoteCommand = "sqlite3 /data/data/com.android.providers.settings/databases/settings.db < ${ANIMATIONS_SQL_FILE_REMOTE_PATH}"
        device.executeShellCommand(remoteCommand, outputReceiver, Cleaner.ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
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