package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import com.android.sdklib.AndroidVersion
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.concurrent.TimeUnit

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*

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
	void testPerformActionApi24() {
		when(device.version).thenReturn(new AndroidVersion(24, 'Nougat'))
		reverter.performAction(device)

		verify(device).executeShellCommand(eq('settings put global window_animation_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global transition_animation_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global animator_duration_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('su 0 pm enable com.android.browser'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('su 0 pm unhide org.chromium.webview_shell'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('su 0 pm unhide com.android.chrome'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('input keyevent 26'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
	}

	@Test
	void testPerformActionApi16() {
		when(device.version).thenReturn(new AndroidVersion(16, 'Jelly Bean'))
		reverter.performAction(device)

		verify(device, never()).executeShellCommand(eq('settings put global window_animation_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('settings put global transition_animation_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('settings put global animator_duration_scale 1'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
	}
}
