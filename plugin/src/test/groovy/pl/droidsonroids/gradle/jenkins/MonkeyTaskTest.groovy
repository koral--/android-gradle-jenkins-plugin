package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.testing.ConnectedDevice
import com.android.builder.testing.api.DeviceProvider
import com.android.ddmlib.IShellOutputReceiver
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import java.util.concurrent.TimeUnit

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*
import static pl.droidsonroids.gradle.jenkins.Constants.*
import static pl.droidsonroids.gradle.jenkins.Constants.BUILD_TOOLS_VERSION
import static pl.droidsonroids.gradle.jenkins.MonkeyTask.MONKEY_TASK_NAME

public class MonkeyTaskTest {

	@Test
	public void testConnectedMonkeyTest() throws Exception {

		def project = ProjectBuilder.builder().build()
		project.pluginManager.apply('pl.droidsonroids.jenkins')
		project.pluginManager.apply('com.android.application')

		project.android {
			defaultConfig {
				applicationId TEST_APPLICATION_ID
				signingConfig signingConfigs.jenkinsRelease
				minSdkVersion 1
			}
			buildToolsVersion BUILD_TOOLS_VERSION
			compileSdkVersion ANDROID_SDK_VERSION
			buildTypes {
				release {
					jenkinsTestable true
				}
			}
		}
		def variant = mock(ApplicationVariant.class, RETURNS_DEEP_STUBS)
		when(variant.getApplicationId()).thenReturn(TEST_APPLICATION_ID)
		def monkeyTask = project.tasks.create(MONKEY_TASK_NAME, MonkeyTask, {
			it.init(Collections.singleton(variant))
		})

		monkeyTask.connectedDeviceProvider = mock(DeviceProvider.class)
		doNothing().when(monkeyTask.connectedDeviceProvider).init()
		ConnectedDevice mockDevice = mock(ConnectedDevice.class)
		when(mockDevice.getName()).thenReturn('testDevice')
		when(mockDevice.getApiLevel()).thenReturn(1)
		doNothing().when(mockDevice).executeShellCommand(anyString(), any(IShellOutputReceiver.class), anyLong(), any(TimeUnit.class))
		when(monkeyTask.connectedDeviceProvider.getDevices()).thenReturn(Collections.singletonList(mockDevice))
		monkeyTask.connectedMonkeyTest()
		verify(mockDevice).executeShellCommand(eq("logcat -v time"), any(IShellOutputReceiver.class), anyLong(), any(TimeUnit.class))
		verify(mockDevice).executeShellCommand(endsWith("${project.android.defaultConfig.applicationId} 1000"), any(IShellOutputReceiver.class), anyLong(), any(TimeUnit.class))
	}
}