package pl.droidsonroids.gradle.jenkins

import com.android.builder.testing.ConnectedDeviceProvider
import com.android.builder.testing.api.DeviceConnector
import com.android.builder.testing.api.DeviceException
import com.android.ddmlib.AdbCommandRejectedException
import com.android.ddmlib.ShellCommandUnresponsiveException
import com.android.utils.ILogger
import com.android.utils.StdLogger

import java.util.concurrent.TimeoutException

import static java.util.concurrent.TimeUnit.SECONDS

public class Cleaner {

    private final LoggerBasedOutputReceiver outputReceiver

    Cleaner(ILogger logger) {
        outputReceiver = new LoggerBasedOutputReceiver(logger)
    }

    static final int ADB_COMMAND_TIMEOUT_SECONDS = 5

    public static void main(String[] args) throws Exception {
        def logger = new StdLogger(StdLogger.Level.VERBOSE)
        def cleaner = new Cleaner(logger)
        cleaner.clean()
    }

    def clean() {
        try {
            cleanConnectedDevices()
        } catch (DeviceException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException | TimeoutException e) {
            outputReceiver.logger.error(e, null)
            System.exit(1)
        }
        catch (Throwable t) {
            outputReceiver.logger.error(t, null)
            System.exit(2)
        }
        System.exit(0)
    }

    def cleanConnectedDevices() {
        def adbLocation = new File(System.getenv('ANDROID_HOME'), 'platform-tools/adb')

        def connectedDeviceProvider = new ConnectedDeviceProvider(adbLocation, outputReceiver.logger)
        connectedDeviceProvider.init()
        connectedDeviceProvider.getDevices().each { device ->
            cleanDevice(device)
        }
        connectedDeviceProvider.terminate()
    }

    def cleanDevice(DeviceConnector device) {
        device.executeShellCommand('pm list packages -3', new AppUninstaller(device, outputReceiver.logger), ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
        device.executeShellCommand('rm -r /sdcard/*', outputReceiver, ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
        device.executeShellCommand('input keyevent 82', outputReceiver, ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
        device.executeShellCommand('input keyevent 4', outputReceiver, ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
    }
}