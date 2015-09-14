package pl.droidsonroids.gradle.jenkins

import com.android.builder.testing.ConnectedDeviceProvider
import com.android.builder.testing.api.DeviceException
import com.android.ddmlib.AdbCommandRejectedException
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.ShellCommandUnresponsiveException
import com.android.utils.ILogger
import com.android.utils.StdLogger

import java.util.concurrent.TimeoutException

import static java.util.concurrent.TimeUnit.SECONDS

public class Cleaner {

    static final int ADB_COMMAND_TIMEOUT_SECONDS = 5

    public static void main(String[] args) throws Exception {
        def logger = new StdLogger(StdLogger.Level.VERBOSE)
        try {
            cleanAllEmulators(logger)
        } catch (DeviceException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException | TimeoutException e) {
            logger.error(e, null)
            System.exit(1)
        }
        catch (Throwable t) {
            logger.error(t, null)
            System.exit(2)
        }
        System.exit(0)
    }

    def static cleanAllEmulators(ILogger logger) {
        def adbLocation = new File(System.getenv('ANDROID_HOME'), 'platform-tools/adb')
        if (!adbLocation.canExecute()) {
            throw new FileNotFoundException('ADB binary not found')
        }

        def connectedDeviceProvider = new ConnectedDeviceProvider(adbLocation, logger)
        connectedDeviceProvider.init()
        connectedDeviceProvider.getDevices()
                .each { device ->
            device.executeShellCommand('pm list packages -3', new AppUninstaller(device, logger), ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
            device.executeShellCommand('rm -r /sdcard/*', new StdOutputReceiver(logger), ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
        }
        connectedDeviceProvider.terminate()
    }
}