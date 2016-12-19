package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import com.android.utils.StdLogger

import java.util.concurrent.TimeUnit

abstract class DeviceActionPerformer {
	private IShellOutputReceiver outputReceiver = new LoggerBasedOutputReceiver(new StdLogger(StdLogger.Level.VERBOSE))

	void executeRemoteCommand(IDevice device, String remoteCommand) {
		device.executeShellCommand(remoteCommand, outputReceiver, Constants.ADB_COMMAND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
	}

	public abstract void performAction(IDevice device)
}
