package pl.droidsonroids.gradle.ci

import com.android.build.gradle.internal.dsl.BuildType
import com.android.builder.model.ProductFlavor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class MonkeyTestExtensionTest {
    private lateinit var monkeyTest: MonkeyTestExtension

    @Before
    fun setUp() {
        monkeyTest = MonkeyTestExtension()
    }

    @Test
    fun `built from product flavor name`() {
        monkeyTest.productFlavors("flavor")
        assertThat(monkeyTest.productFlavorNames).containsOnly("flavor")
    }

    @Test
    fun `built from product flavor`() {
        val flavor = mock<ProductFlavor> {
            on { name } doReturn "flavor"
        }
        monkeyTest.productFlavors(flavor)
        assertThat(monkeyTest.productFlavorNames).containsOnly("flavor")
    }

    @Test
    fun `built from build type name`() {
        monkeyTest.buildTypes("type")
        assertThat(monkeyTest.buildTypeNames).containsOnly("type")
    }

    @Test
    fun `built from build type`() {
        val type = mock<BuildType> {
            on { name } doReturn "type"
        }
        monkeyTest.buildTypes(type)
        assertThat(monkeyTest.buildTypeNames).containsOnly("type")
    }

    @Test
    fun `built from variants`() {
        monkeyTest.applicationVariants("flavorType")
        assertThat(monkeyTest.variantNames).containsOnly("flavorType")
    }

}
