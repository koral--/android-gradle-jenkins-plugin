package pl.droidsonroids.gradle.ci

import com.android.ddmlib.IDevice
import com.android.sdklib.AndroidVersion
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class DeviceSetuperTest {
    @Mock
    private lateinit var device: IDevice
    private lateinit var setuper: DeviceSetuper

    private val remotePath = "/test/"
    private val fileName = "image_portrait.jpg"

    @Before
    fun setUp() {
        setuper = DeviceSetuper()
    }

    @Test
    fun `API 24 actions performed`() {
        whenever(device.version).thenReturn(AndroidVersion(24, "Nougat"))
        setuper.doWork(device)

        verify(device).executeShellCommand(eq("settings put global window_animation_scale 0"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("settings put global transition_animation_scale 0"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("settings put global animator_duration_scale 0"), any(), anyLong(), any())
        verify(device, atLeastOnce()).pushFile(anyString(), ArgumentMatchers.anyString())
        verify(device, atLeastOnce()).executeShellCommand(Mockito.argThat { it.startsWith(Constants.MEDIA_SCAN_COMMAND) }, any(), anyLong(), any())
        verify(device).executeShellCommand(eq("su 0 pm disable com.android.browser"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("su 0 pm hide org.chromium.webview_shell"), any(), anyLong(), any())

        verify(device).executeShellCommand(eq("wm dismiss-keyguard"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("input keyevent 82"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("input swipe 0 200 0 0"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("input swipe 0 500 0 0"), any(), anyLong(), any())
    }

    @Test
    fun `API 22 actions performed`() {
        whenever(device.version).thenReturn(AndroidVersion(22, "Lollipop"))
        setuper.doWork(device)

        verify(device, never()).executeShellCommand(eq("wm dismiss-keyguard"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("input keyevent 82"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("input swipe 0 200 0 0"), any(), anyLong(), any())
        verify(device).executeShellCommand(eq("input swipe 0 500 0 0"), any(), anyLong(), any())
    }

    @Test
    fun `API 16 actions performed`() {
        whenever(device.version).thenReturn(AndroidVersion(16, "Jelly Bean"))
        setuper.doWork(device)

        verify(device, never()).executeShellCommand(eq("wm dismiss-keyguard"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("settings put global window_animation_scale 0"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("settings put global transition_animation_scale 0"), any(), anyLong(), any())
        verify(device, never()).executeShellCommand(eq("settings put global animator_duration_scale 0"), any(), anyLong(), any())
    }

    @Test
    fun `file pushed`() {
        val remoteFilePath = setuper.pushFile(device, fileName, remotePath)

        assertThat(File(remoteFilePath)).hasParent(remotePath).hasName(fileName)

        val captor = ArgumentCaptor.forClass(String::class.java)
        verify(device).pushFile(captor.capture(), eq(remoteFilePath))
        assertThat(captor.value).endsWith(fileName)
    }

    @Test
    fun `temporary directory deleted on shutdown`() {
        assertThat(setuper.tempDir).isDirectory()

        setuper.pushFile(device, fileName, remotePath)
        setuper.pushFile(device, fileName, remotePath)

        setuper.cleanTempDirectory()
        assertThat(setuper.tempDir).doesNotExist()
    }
}
