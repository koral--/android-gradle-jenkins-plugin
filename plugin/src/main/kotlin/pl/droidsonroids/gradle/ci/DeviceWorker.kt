package pl.droidsonroids.gradle.ci

import com.android.ddmlib.IDevice
import com.android.utils.StdLogger
import java.util.concurrent.TimeUnit

abstract class DeviceWorker {
    protected var logger = StdLogger(StdLogger.Level.VERBOSE)
    private val outputReceiver = LoggingOutputReceiver(logger)

    fun IDevice.executeRemoteCommand(remoteCommand: String) =
            executeShellCommand(remoteCommand, outputReceiver, Constants.ADB_COMMAND_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)

    abstract fun doWork(device: IDevice)
}
