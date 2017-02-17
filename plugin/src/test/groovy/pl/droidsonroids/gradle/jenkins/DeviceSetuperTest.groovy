package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import com.android.sdklib.AndroidVersion
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.concurrent.TimeUnit

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*

class DeviceSetuperTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule()
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder()

	@Mock
	IDevice device
	private DeviceSetuper setuper

	@Before
	void setUp() {
		setuper = new DeviceSetuper()
	}

	@Test
	void testPerformActionApi24() {
		when(device.version).thenReturn(new AndroidVersion(24, 'Nougat'))
		setuper.performAction(device)

		verify(device).executeShellCommand(eq('settings put global window_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global transition_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global animator_duration_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, atLeastOnce()).pushFile(any(String.class), any(String.class))
		verify(device, atLeastOnce()).executeShellCommand(startsWith(Constants.MEDIA_SCAN_COMMAND), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('su 0 pm disable com.android.browser'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('su 0 pm hide org.chromium.webview_shell'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))

		verify(device).executeShellCommand(eq('wm dismiss-keyguard'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('input keyevent 82'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('input swipe 0 200 0 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('input swipe 0 500 0 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
	}

	@Test
	void testPerformActionApi22() {
		when(device.version).thenReturn(new AndroidVersion(22, 'Lollipop'))
		setuper.performAction(device)

		verify(device, never()).executeShellCommand(eq('wm dismiss-keyguard'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('input keyevent 82'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('input swipe 0 200 0 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('input swipe 0 500 0 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
	}

	@Test
	void testPerformActionApi16() {
		when(device.version).thenReturn(new AndroidVersion(16, 'Jelly Bean'))
		setuper.performAction(device)

		verify(device, never()).executeShellCommand(eq('wm dismiss-keyguard'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('settings put global window_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('settings put global transition_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, never()).executeShellCommand(eq('settings put global animator_duration_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
	}

	@Test
	void testPushFile() {
		def remotePath = '/test/'

		def fileName = 'image_portrait.jpg'
		def remoteFilePath = setuper.pushFile(device, fileName, remotePath)

		assertThat(new File(remoteFilePath)).hasParent(remotePath).hasName(fileName)
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class)
		verify(device).pushFile(captor.capture(), eq(remoteFilePath))
		assertThat(captor.value).endsWith(fileName)
	}
}
