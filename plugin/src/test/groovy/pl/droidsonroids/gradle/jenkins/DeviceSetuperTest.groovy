package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
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
import static org.mockito.Mockito.atLeastOnce
import static org.mockito.Mockito.verify

class DeviceSetuperTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule()
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder()

	@Mock
	IDevice device
	private DeviceSetuper setuper

	@Before
	public void setUp() {
		setuper = new DeviceSetuper()
	}

	@Test
	void testPerformAction() {
		setuper.performAction(device)

		verify(device).executeShellCommand(eq('settings put global window_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global transition_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global animator_duration_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, atLeastOnce()).pushFile(any(), any())
		verify(device, atLeastOnce()).executeShellCommand(startsWith(Constants.MEDIA_SCAN_COMMAND), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('su 0 pm disable com.android.browser'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('su 0 pm hide org.chromium.webview_shell'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
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
