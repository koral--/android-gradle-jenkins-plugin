package pl.droidsonroids.gradle.jenkins

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UiTestModeTest {
    @Test
    fun testNoMinifyGetMinifyEnabled() {
        assertThat(UiTestMode.noMinify.getMinifyEnabled(true)).isFalse()
        assertThat(UiTestMode.noMinify.getMinifyEnabled(false)).isFalse()
    }

    @Test
    fun testMinifyGetMinifyEnabled() {
        assertThat(UiTestMode.minify.getMinifyEnabled(true)).isTrue()
        assertThat(UiTestMode.minify.getMinifyEnabled(false)).isFalse()
    }

}
