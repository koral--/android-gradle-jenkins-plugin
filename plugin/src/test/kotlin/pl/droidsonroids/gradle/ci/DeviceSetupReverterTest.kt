package pl.droidsonroids.gradle.ci

import com.android.ddmlib.IDevice
import com.android.sdklib.AndroidVersion
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DeviceSetupReverterTest {

    @Mock
    private lateinit var device: IDevice
    private lateinit var reverter: DeviceSetupReverter

    @Before
    fun setUp() {
        reverter = DeviceSetupReverter()
    }

    @Test
    fun `API 24 actions performed`() {
        whenever(device.version).thenReturn(AndroidVersion(24, "Nougat"))
        reverter.doWork(device)

        verify(device).executeShellCommand(eq("settings put global window_animation_scale 1"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("settings put global transition_animation_scale 1"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("settings put global animator_duration_scale 1"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("su 0 pm enable com.android.browser"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("su 0 pm unhide org.chromium.webview_shell"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("su 0 pm unhide com.android.chrome"), any(), anyLong(), any())
    }

    @Test
    fun `API 16 actions performed`() {
        whenever(device.version).thenReturn(AndroidVersion(16, "Jelly Bean"))
        reverter.doWork(device)

        verify(device, never()).executeShellCommand(eq("settings put global window_animation_scale 1"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("settings put global transition_animation_scale 1"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("settings put global animator_duration_scale 1"), any(), anyLong(), any())
    }
}
