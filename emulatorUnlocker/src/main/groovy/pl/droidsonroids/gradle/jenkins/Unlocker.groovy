package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.utils.StdLogger

import static java.util.concurrent.TimeUnit.SECONDS

public class Unlocker {

    static final int ADB_COMMAND_TIMEOUT_SECONDS = 5
    static def outputReceiver = new StdOutputReceiver(new StdLogger(StdLogger.Level.VERBOSE))


    public static void main(String[] args) throws Exception {

        def adbLocation = new File(System.getenv('ANDROID_HOME'), 'platform-tools/adb')
        if (!adbLocation.canExecute()) {
            throw new FileNotFoundException('ADB binary not found')
        }

        AndroidDebugBridge.initIfNeeded(false)
        def bridge = AndroidDebugBridge.createBridge(adbLocation.absolutePath, false)
        bridge.hasInitialDeviceList()
        bridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
            @Override
            void deviceConnected(IDevice device) {
                println 'conn ' + device.name
                unlock(device)
            }

            @Override
            void deviceDisconnected(IDevice device) {
                println 'dc ' + device.name
            }

            @Override
            void deviceChanged(IDevice device, int changeMask) {
                println changeMask + " " + device.state
                if (changeMask == IDevice.CHANGE_STATE) {
                    unlock(device)
                }
            }

        })
        bridge.getDevices().each {
            unlock(it)
        }

        synchronized (Unlocker.class) {
            while (true) {
                wait()
            }
        }
    }

    static def unlock(IDevice device) {
        if (!device.isOnline()) {
            return
        }
        device.executeShellCommand('input keyevent 82', outputReceiver, Cleaner.ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
        device.executeShellCommand('input keyevent 4', outputReceiver, Cleaner.ADB_COMMAND_TIMEOUT_SECONDS, SECONDS)
        //TODO disable animations
    }

}