package pl.droidsonroids.gradle.jenkins

import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor

open class MonkeyTestExtension {
    val productFlavorNames: MutableSet<String> = mutableSetOf()
    val buildTypeNames: MutableSet<String> = mutableSetOf()
    val variantNames: MutableSet<String> = mutableSetOf()

    fun productFlavors(vararg productFlavors: ProductFlavor) {
        productFlavorNames.addAll(productFlavors.map { it.name })
    }

    fun productFlavors(vararg productFlavors: String) {
        productFlavorNames.addAll(productFlavors)
    }

    fun buildTypes(vararg buildTypes: BuildType) {
        buildTypeNames.addAll(buildTypes.map { it.name })
    }

    fun buildTypes(vararg buildTypes: String) {
        buildTypeNames.addAll(buildTypes)
    }

    fun applicationVariants(vararg variantNames: String) {
        this.variantNames.addAll(variantNames)
    }
}
