package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.internal.dsl.BuildType
import com.android.builder.model.ProductFlavor
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class TestableExtensionTest {
	private TestableExtension jenkinsTestable

	@Before
	public void setUp() {
		jenkinsTestable = new TestableExtension()
	}

	@Test
	void testProductFlavorsString() {
		jenkinsTestable.productFlavors('flavor')
		assertThat(jenkinsTestable.productFlavorNames).containsOnly('flavor')
	}

	@Test
	void testProductFlavors() {
		def flavor = mock(ProductFlavor)
		when(flavor.getName()).thenReturn('flavor')
		jenkinsTestable.productFlavors(flavor)
		assertThat(jenkinsTestable.productFlavorNames).containsOnly('flavor')
	}

	@Test
	void testBuildTypesString() {
		jenkinsTestable.buildTypes('type')
		assertThat(jenkinsTestable.buildTypeNames).containsOnly('type')
	}

	@Test
	void testBuildTypes() {
		def type = mock(BuildType)
		when(type.getName()).thenReturn('type')
		jenkinsTestable.buildTypes(type)
		assertThat(jenkinsTestable.buildTypeNames).containsOnly('type')
	}

	@Test
	void testApplicationVariants() {
		jenkinsTestable.applicationVariants('flavorType')
		assertThat(jenkinsTestable.variantNames).containsOnly('flavorType')
	}

	@Test
	void testTestInstrumentationRunner() {
		jenkinsTestable.testInstrumentationRunner('test.example.Runner')
		assertThat(jenkinsTestable.testInstrumentationRunner).isEqualTo('test.example.Runner')
	}
}
