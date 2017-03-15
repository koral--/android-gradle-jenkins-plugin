package pl.droidsonroids.gradle.ci

import com.android.build.gradle.api.ApplicationVariant
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.RETURNS_DEEP_STUBS

class UiTestExtensionTest {
    private lateinit var variant: ApplicationVariant
    private lateinit var applicationVariants: Collection<ApplicationVariant>
    private lateinit var uiTest: UiTestExtension

    @Before
    fun setUp() {
        variant = mock<ApplicationVariant>(defaultAnswer = RETURNS_DEEP_STUBS)
        uiTest = UiTestExtension()
        applicationVariants = setOf(variant)
    }

    @Test
    fun `test instrumentation runner change applied`() {
        uiTest.testInstrumentationRunner("test.example.Runner")
        assertThat(uiTest.testInstrumentationRunner).isEqualTo("test.example.Runner")
    }

    @Test
    fun `minifyEnabled change applied`() {
        assertThat(uiTest.minifyEnabled).isNull()
        uiTest.minifyEnabled(true)
        assertThat(uiTest.minifyEnabled).isTrue()
        uiTest.minifyEnabled(false)
        assertThat(uiTest.minifyEnabled).isFalse()
    }

    @Test
    fun `minifyEnabled has valid default value with no minified variants`() {
        assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isFalse()
        uiTest.minifyEnabled(true)
        assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isTrue()
        uiTest.minifyEnabled(false)
        assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isFalse()
    }

    @Test
    fun `minifyEnabled has valid default with minified variants`() {
        whenever(variant.buildType.isMinifyEnabled).thenReturn(true)

        assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isTrue()
        uiTest.minifyEnabled(true)
        assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isTrue()
        uiTest.minifyEnabled(false)
        assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isFalse()
    }
}
