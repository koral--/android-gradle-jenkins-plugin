package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.verify
import static pl.droidsonroids.gradle.jenkins.Constants.ADB_COMMAND_TIMEOUT_MILLIS

class DeviceActionPerformerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule()
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder()

	@Mock
	IDevice device
	private DeviceActionPerformer performer

	@Before
	public void setUp() {
		performer = new DeviceActionPerformer() {
			@Override
			void performAction(IDevice device) {
				throw new UnsupportedOperationException('not implemented')
			}
		}
	}

	@Test
	void testExecuteRemoteCommand() {
		def command = 'test'

		performer.executeRemoteCommand(device, command)

		verify(device).executeShellCommand(eq(command), any(), eq((long) ADB_COMMAND_TIMEOUT_MILLIS), eq(MILLISECONDS))
	}
}
