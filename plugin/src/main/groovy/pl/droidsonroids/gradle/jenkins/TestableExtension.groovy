package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.builder.model.BuildType

public class TestableExtension {
	final Set<String> productFlavorNames = []
	final Set<String> buildTypeNames = []
	final Set<String> variantNames = []

	public void productFlavors(ProductFlavor... productFlavors) {
		productFlavorNames.addAll(productFlavors.collect { it.name })
	}

	public void productFlavors(String... productFlavorNames) {
		this.productFlavorNames.addAll(productFlavorNames)
	}

	public void buildTypes(BuildType... buildTypes) {
		buildTypeNames.addAll(buildTypes.collect { it.name })
	}

	public void buildTypes(String... buildTypeNames) {
		this.buildTypeNames.addAll(buildTypeNames)
	}

	public void applicationVariants(String... variantNames){
		this.variantNames.addAll(variantNames)
	}
}
