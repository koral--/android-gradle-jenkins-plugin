package pl.droidsonroids.gradle.jenkins

import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor

public class TestableExtension {
	final Set<String> productFlavorNames = []
	final Set<String> buildTypeNames = []
	final Set<String> variantNames = []
	String testInstrumentationRunner

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

	public void applicationVariants(String... variantNames) {
		this.variantNames.addAll(variantNames)
	}

	public testInstrumentationRunner(String testInstrumentationRunner) {
		this.testInstrumentationRunner = testInstrumentationRunner
	}
}
