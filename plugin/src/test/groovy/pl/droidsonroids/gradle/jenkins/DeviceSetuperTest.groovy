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

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*
import static pl.droidsonroids.gradle.jenkins.Constants.ADB_COMMAND_TIMEOUT_MILLIS

class DeviceSetuperTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule()
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder()

	@Mock
	IDevice device
	private DeviceSetuper setuper
	private File folder

	@Before
	public void setUp() {
		folder = temporaryFolder.newFolder()
		setuper = new DeviceSetuper(folder)
	}

	@Test
	void testSetupApi24() {
		when(device.getVersion()).thenReturn(new AndroidVersion('24'))

		setuper.setup(device)

		verify(device).executeShellCommand(eq('settings put global window_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global transition_animation_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('settings put global animator_duration_scale 0'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device, atLeastOnce()).pushFile(any(), any())
		verify(device, atLeastOnce()).executeShellCommand(startsWith(Constants.MEDIA_SCAN_COMMAND), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('pm disable com.android.browser'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
		verify(device).executeShellCommand(eq('pm hide org.chromium.webview_shell'), any(IShellOutputReceiver), anyLong(), any(TimeUnit))
	}

	@Test
	void testPushFile() {
		def remotePath = '/test/'

		def fileName = 'image_portrait.jpg'
		def remoteFilePath = setuper.pushFile(device, fileName, remotePath)

		assertThat(new File(remoteFilePath)).hasParent(remotePath).hasName(fileName)
		verify(device).pushFile(new File(folder, fileName).path, remoteFilePath)
	}

	@Test
	void testExecuteRemoteCommand() {
		def command = 'test'

		setuper.executeRemoteCommand(device, command)

		verify(device).executeShellCommand(eq(command), any(), eq((long) ADB_COMMAND_TIMEOUT_MILLIS), eq(MILLISECONDS))
	}
}
