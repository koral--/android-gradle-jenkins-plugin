package pl.droidsonroids.gradle.ci

import com.android.utils.ILogger
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoggingOutputReceiverTest {
    @Mock
    lateinit var logger: ILogger
    @InjectMocks
    lateinit var receiver: LoggingOutputReceiver

    @Test
    fun `new lines passed to logger`() {
        receiver.processNewLines(arrayOf("a", "b"))
        verify(logger).info("a")
        verify(logger).info("b")
    }

    @Test
    fun `no lines not passed to logger`() {
        receiver.processNewLines(emptyArray())
        verify(logger, never()).info(any())
    }

    @Test
    fun `is not cancelled by default`() {
        assertThat(receiver.isCancelled).isFalse()
    }
}