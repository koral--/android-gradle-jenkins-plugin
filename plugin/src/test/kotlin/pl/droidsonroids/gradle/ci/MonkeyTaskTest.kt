package pl.droidsonroids.gradle.ci

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.core.DefaultApiVersion
import com.android.builder.testing.ConnectedDevice
import com.android.builder.testing.api.DeviceProvider
import com.android.ddmlib.IShellOutputReceiver
import com.nhaarman.mockito_kotlin.*
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import pl.droidsonroids.gradle.ci.Constants.MONKEY_TASK_NAME
import java.util.*
import java.util.concurrent.TimeUnit

class MonkeyTaskTest {
    @Test
    fun `monkey task performs monkeying`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("pl.droidsonroids.jenkins")
        project.pluginManager.apply("com.android.application")

        project.extensions.configure(AppExtension::class.java) { android ->
            android.defaultConfig {
                it.applicationId = "pl.droidsonroids.testapplication"
                it.signingConfig = android.signingConfigs.getByName("jenkinsRelease")
                it.minSdkVersion = DefaultApiVersion(1)
            }
            android.buildToolsVersion = "25.0.2"
            android.compileSdkVersion = "android-25"
        }
        project.extensions.configure(MonkeyTestExtension::class.java) {
            it.buildTypes("release")
        }
        val variant = mock<ApplicationVariant>(defaultAnswer = RETURNS_DEEP_STUBS)
        whenever(variant.applicationId).thenReturn("pl.droidsonroids.testapplication")
        val monkeyTask = project.tasks.create(MONKEY_TASK_NAME, MonkeyTask::class.java, {
            it.appExtension(project.getAndroidExtension<AppExtension>())
            it.testableVariants = setOf(variant)
        })

        monkeyTask.connectedDeviceProvider = mock<DeviceProvider>()
        doNothing().whenever(monkeyTask.connectedDeviceProvider).init()

        val mockDevice = mock<ConnectedDevice>()
        whenever(mockDevice.name).thenReturn("testDevice")
        whenever(mockDevice.apiLevel).thenReturn(1)
        doNothing().whenever(mockDevice).executeShellCommand(anyString(), any<IShellOutputReceiver>(), anyLong(), any<TimeUnit>())
        whenever(monkeyTask.connectedDeviceProvider.devices).thenReturn(Collections.singletonList(mockDevice))
        monkeyTask.connectedMonkeyTest()

        verify(mockDevice).executeShellCommand(eq("logcat -v time"), any(), anyLong(), any<TimeUnit>())
        verify(mockDevice).executeShellCommand(Mockito.endsWith("${project.project.getAndroidExtension<AppExtension>().defaultConfig.applicationId} 1000"), any<IShellOutputReceiver>(), anyLong(), any<TimeUnit>())
    }
}