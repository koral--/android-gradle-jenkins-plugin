package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.internal.dsl.BuildType
import com.android.builder.model.ProductFlavor
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class MonkeyTestExtensionTest {
	private MonkeyTestExtension monkeyTest

	@Before
    void setUp() {
		monkeyTest = new MonkeyTestExtension()
	}

	@Test
    void testProductFlavorsString() {
		monkeyTest.productFlavors('flavor')
		assertThat(monkeyTest.productFlavorNames).containsOnly('flavor')
	}

	@Test
    void testProductFlavors() {
		def flavor = mock(ProductFlavor)
		when(flavor.getName()).thenReturn('flavor')
		monkeyTest.productFlavors(flavor)
		assertThat(monkeyTest.productFlavorNames).containsOnly('flavor')
	}

	@Test
    void testBuildTypesString() {
		monkeyTest.buildTypes('type')
		assertThat(monkeyTest.buildTypeNames).containsOnly('type')
	}

	@Test
    void testBuildTypes() {
		def type = mock(BuildType)
		when(type.getName()).thenReturn('type')
		monkeyTest.buildTypes(type)
		assertThat(monkeyTest.buildTypeNames).containsOnly('type')
	}

	@Test
    void testApplicationVariants() {
		monkeyTest.applicationVariants('flavorType')
		assertThat(monkeyTest.variantNames).containsOnly('flavorType')
	}
}
