package pl.droidsonroids.gradle.jenkins

import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor

class MonkeyTestExtension {
	final Set<String> productFlavorNames = []
	final Set<String> buildTypeNames = []
	final Set<String> variantNames = []

    void productFlavors(ProductFlavor... productFlavors) {
		productFlavorNames.addAll(productFlavors.collect { it.name })
	}

    void productFlavors(String... productFlavorNames) {
		this.productFlavorNames.addAll(productFlavorNames)
	}

    void buildTypes(BuildType... buildTypes) {
		buildTypeNames.addAll(buildTypes.collect { it.name })
	}

    void buildTypes(String... buildTypeNames) {
		this.buildTypeNames.addAll(buildTypeNames)
	}

    void applicationVariants(String... variantNames) {
		this.variantNames.addAll(variantNames)
	}
}
