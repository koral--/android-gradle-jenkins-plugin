package pl.droidsonroids.gradle.ci

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Test
import org.mockito.Mockito.CALLS_REAL_METHODS


class BaseOutputReceiverTest {
    @Test
    fun `is not cancelled by default`() {
        val receiver = mock<BaseOutputReceiver>(defaultAnswer = CALLS_REAL_METHODS)
        assertThat(receiver.isCancelled).isFalse()
    }
}
