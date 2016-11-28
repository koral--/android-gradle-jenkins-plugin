package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.internal.dsl.BuildType
import com.android.builder.model.ProductFlavor
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class MonkeyTestExtensionTest {
	private MonkeyTestExtension monkeyTest

	@Before
	public void setUp() {
		monkeyTest = new MonkeyTestExtension()
	}

	@Test
	public void testProductFlavorsString() {
		monkeyTest.productFlavors('flavor')
		assertThat(monkeyTest.productFlavorNames).containsOnly('flavor')
	}

	@Test
	public void testProductFlavors() {
		def flavor = mock(ProductFlavor)
		when(flavor.getName()).thenReturn('flavor')
		monkeyTest.productFlavors(flavor)
		assertThat(monkeyTest.productFlavorNames).containsOnly('flavor')
	}

	@Test
	public void testBuildTypesString() {
		monkeyTest.buildTypes('type')
		assertThat(monkeyTest.buildTypeNames).containsOnly('type')
	}

	@Test
	public void testBuildTypes() {
		def type = mock(BuildType)
		when(type.getName()).thenReturn('type')
		monkeyTest.buildTypes(type)
		assertThat(monkeyTest.buildTypeNames).containsOnly('type')
	}

	@Test
	public void testApplicationVariants() {
		monkeyTest.applicationVariants('flavorType')
		assertThat(monkeyTest.variantNames).containsOnly('flavorType')
	}
}
