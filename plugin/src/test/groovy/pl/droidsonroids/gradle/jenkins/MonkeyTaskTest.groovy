package pl.droidsonroids.gradle.jenkins

import com.android.builder.testing.ConnectedDevice
import com.android.builder.testing.api.DeviceProvider
import com.android.ddmlib.IShellOutputReceiver
import org.junit.Test

import java.util.concurrent.TimeUnit

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*

public class MonkeyTaskTest extends BasePluginTest {

	@Test
	public void testConnectedMonkeyTest() throws Exception {
		android.buildTypes {
			release {
				jenkinsTestable true
			}
		}

		project.evaluate()

		MonkeyTask monkeyTask = project.tasks.getByName(MonkeyTask.MONKEY_TASK_NAME) as MonkeyTask
		monkeyTask.connectedDeviceProvider = mock(DeviceProvider.class)
		doNothing().when(monkeyTask.connectedDeviceProvider).init()
		ConnectedDevice mockDevice = mock(ConnectedDevice.class)
		when(mockDevice.getName()).thenReturn('testDevice')
		when(mockDevice.getApiLevel()).thenReturn(1)
		doNothing().when(mockDevice).executeShellCommand(anyString(), any(IShellOutputReceiver.class), anyLong(), any(TimeUnit.class))
		when(monkeyTask.connectedDeviceProvider.getDevices()).thenReturn(Collections.singletonList(mockDevice))
		monkeyTask.connectedMonkeyTest()
		verify(mockDevice).executeShellCommand(endsWith("${android.defaultConfig.applicationId} 1000"), any(IShellOutputReceiver.class), anyLong(), any(TimeUnit.class))
	}
}