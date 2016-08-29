package pl.droidsonroids.gradle.jenkins

class MonkeyTestableUnits {
	final Map<String, Boolean> buildTypes
	final PersistentMap productFlavors

	MonkeyTestableUnits(File buildTypesDir, File productFlavorsDir) {
		buildTypes = new PersistentMap(buildTypesDir)
		productFlavors = new PersistentMap(productFlavorsDir)
	}
}