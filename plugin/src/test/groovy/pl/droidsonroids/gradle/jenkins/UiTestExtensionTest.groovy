package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.api.ApplicationVariant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Answers.RETURNS_DEEP_STUBS
import static org.mockito.Mockito.when

class UiTestExtensionTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule()
	@Mock(answer = RETURNS_DEEP_STUBS)
	private ApplicationVariant variant

	private Collection<ApplicationVariant> applicationVariants

	private UiTestExtension uiTest

	@Before
    void setUp() {
		uiTest = new UiTestExtension()
		applicationVariants = Collections.singleton(variant)
	}

	@Test
    void testTestInstrumentationRunner() {
		uiTest.testInstrumentationRunner('test.example.Runner')
		assertThat(uiTest.testInstrumentationRunner).isEqualTo('test.example.Runner')
	}

	@Test
    void testMinifyEnabled() {
		assertThat(uiTest.minifyEnabled).isNull()
		uiTest.minifyEnabled(true)
		assertThat(uiTest.minifyEnabled).isTrue()
		uiTest.minifyEnabled(false)
		assertThat(uiTest.minifyEnabled).isFalse()
	}

	@Test
    void testGetDefaultMinifyEnabledNoMinifiedVariants() {
		assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isFalse()
		uiTest.minifyEnabled true
		assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isTrue()
		uiTest.minifyEnabled false
		assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isFalse()
	}

	@Test
    void testGetDefaultMinifyEnabledWithMinifiedVariants() {
		when(variant.buildType.minifyEnabled).thenReturn(true)

		assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isTrue()
		uiTest.minifyEnabled true
		assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isTrue()
		uiTest.minifyEnabled false
		assertThat(uiTest.getDefaultMinifyEnabled(applicationVariants)).isFalse()
	}
}
