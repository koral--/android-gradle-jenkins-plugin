package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BuildType
import com.android.builder.model.ProductFlavor
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Answers.RETURNS_DEEP_STUBS
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class TestableExtensionTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule()
	@Mock(answer = RETURNS_DEEP_STUBS)
	private ApplicationVariant variant

	private Collection<ApplicationVariant> applicationVariants

	private TestableExtension jenkinsTestable

	@Before
	public void setUp() {
		jenkinsTestable = new TestableExtension()
		applicationVariants = Collections.singleton(variant)
	}

	@Test
	public void testProductFlavorsString() {
		jenkinsTestable.productFlavors('flavor')
		assertThat(jenkinsTestable.productFlavorNames).containsOnly('flavor')
	}

	@Test
	public void testProductFlavors() {
		def flavor = mock(ProductFlavor)
		when(flavor.getName()).thenReturn('flavor')
		jenkinsTestable.productFlavors(flavor)
		assertThat(jenkinsTestable.productFlavorNames).containsOnly('flavor')
	}

	@Test
	public void testBuildTypesString() {
		jenkinsTestable.buildTypes('type')
		assertThat(jenkinsTestable.buildTypeNames).containsOnly('type')
	}

	@Test
	public void testBuildTypes() {
		def type = mock(BuildType)
		when(type.getName()).thenReturn('type')
		jenkinsTestable.buildTypes(type)
		assertThat(jenkinsTestable.buildTypeNames).containsOnly('type')
	}

	@Test
	public void testApplicationVariants() {
		jenkinsTestable.applicationVariants('flavorType')
		assertThat(jenkinsTestable.variantNames).containsOnly('flavorType')
	}

	@Test
	public void testTestInstrumentationRunner() {
		jenkinsTestable.testInstrumentationRunner('test.example.Runner')
		assertThat(jenkinsTestable.testInstrumentationRunner).isEqualTo('test.example.Runner')
	}

	@Test
	public void testMinifyEnabled() {
		assertThat(jenkinsTestable.minifyEnabled).isNull()
		jenkinsTestable.minifyEnabled(true)
		assertThat(jenkinsTestable.minifyEnabled).isTrue()
		jenkinsTestable.minifyEnabled(false)
		assertThat(jenkinsTestable.minifyEnabled).isFalse()
	}

	@Test
	public void testGetDefaultMinifyEnabledNoMinifiedVariants() {
		assertThat(jenkinsTestable.getDefaultMinifyEnabled(applicationVariants)).isFalse()
		jenkinsTestable.minifyEnabled true
		assertThat(jenkinsTestable.getDefaultMinifyEnabled(applicationVariants)).isTrue()
		jenkinsTestable.minifyEnabled false
		assertThat(jenkinsTestable.getDefaultMinifyEnabled(applicationVariants)).isFalse()
	}

	@Test
	public void testGetDefaultMinifyEnabledWithMinifiedVariants() {
		when(variant.buildType.minifyEnabled).thenReturn(true)

		assertThat(jenkinsTestable.getDefaultMinifyEnabled(applicationVariants)).isTrue()
		jenkinsTestable.minifyEnabled true
		assertThat(jenkinsTestable.getDefaultMinifyEnabled(applicationVariants)).isTrue()
		jenkinsTestable.minifyEnabled false
		assertThat(jenkinsTestable.getDefaultMinifyEnabled(applicationVariants)).isFalse()
	}
}
