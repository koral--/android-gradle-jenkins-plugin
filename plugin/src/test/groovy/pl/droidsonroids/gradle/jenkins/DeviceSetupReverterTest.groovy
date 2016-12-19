package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.concurrent.TimeUnit

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.verify

class DeviceSetupReverterTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule()
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder()

	@Mock
	IDevice device
	private DeviceSetupReverter reverter

	@Before
	public void setUp() {
		reverter = new DeviceSetupReverter()
	}

	@Test
	void testPerformAction() {
		reverter.performAction(device)

		verify(device).executeShellCommand(eq('settings put global window_animation_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global transition_animation_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global animator_duration_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
	}
}
