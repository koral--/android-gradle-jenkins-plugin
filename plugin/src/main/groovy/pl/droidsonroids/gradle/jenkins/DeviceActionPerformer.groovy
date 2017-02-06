package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import com.android.utils.StdLogger

import java.util.concurrent.TimeUnit

abstract class DeviceActionPerformer {
	protected StdLogger logger = new StdLogger(StdLogger.Level.VERBOSE)
	private IShellOutputReceiver outputReceiver = new LoggerBasedOutputReceiver(logger)

	void executeRemoteCommand(IDevice device, String remoteCommand) {
		device.executeShellCommand(remoteCommand, outputReceiver, Constants.ADB_COMMAND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
	}

    abstract void performAction(IDevice device)
}
