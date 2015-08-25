package pl.droidsonroids.gradle.jenkins

import com.android.builder.testing.ConnectedDeviceProvider
import com.android.utils.StdLogger

import java.util.concurrent.TimeUnit

class Cleaner {

    static class StdOutputReceiver extends BaseOutputReceiver {
        StdLogger logger = new StdLogger(StdLogger.Level.VERBOSE)

        @Override
        void processNewLines(String[] lines) {
            lines.each { logger.println(it) }
        }
    }

    public static void main(String[] args) {
        def stdOutputReceiver = new StdOutputReceiver()
        def adbLocation = new File(System.getenv('ANDROID_HOME'), 'platform-tools/adb')
        def connectedDeviceProvider = new ConnectedDeviceProvider(adbLocation, new StdLogger(StdLogger.Level.INFO))
        connectedDeviceProvider.init()
        connectedDeviceProvider.getDevices().each { device ->
            BaseOutputReceiver outputReceiver = new BaseOutputReceiver() {
                @Override
                void processNewLines(String[] lines) {
                    lines.findAll {
                        it.startsWith('package:')
                    }.each {
                        def appId = it.split(':', -1)[1]
                        device.executeShellCommand("pm uninstall ${appId}", stdOutputReceiver, 5, TimeUnit.SECONDS)
                    }
                }
            }
            device.executeShellCommand('pm list packages -3', outputReceiver, 5, TimeUnit.SECONDS)
            device.executeShellCommand('rm -rf /sdcard/', outputReceiver, 5, TimeUnit.SECONDS)
        }
        connectedDeviceProvider.terminate()
        System.exit(0)
    }
}