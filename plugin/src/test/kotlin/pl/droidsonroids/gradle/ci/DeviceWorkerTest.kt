package pl.droidsonroids.gradle.ci

import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class DeviceWorkerTest {
    private lateinit var performer: DeviceWorker

    @Before
    fun setUp() {
        performer = object : DeviceWorker() {
            override fun doWork(device: IDevice) = TODO()

        }
    }

    @Test
    fun `remote command executed`() {
        val command = "test"
        val device = mock<IDevice>()
        performer.executeRemoteCommand(device, command)

        verify(device).executeShellCommand(eq(command), any(), any(), any())
    }
}
