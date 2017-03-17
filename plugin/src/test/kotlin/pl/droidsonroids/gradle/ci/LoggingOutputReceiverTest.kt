package pl.droidsonroids.gradle.ci

import com.android.utils.ILogger
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class LoggingOutputReceiverTest {
    @Test
    fun processNewLines() {
        val logger = mock<ILogger>()
        LoggingOutputReceiver(logger).processNewLines(arrayOf("a", "b"))
        verify(logger).info("a")
        verify(logger).info("b")
    }
}