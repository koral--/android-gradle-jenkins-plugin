package pl.droidsonroids.gradle.jenkins

class MonkeyTestableUnits {
	final Map<String, Boolean> buildTypes
	final PersistentBooleanMap productFlavors

	MonkeyTestableUnits(File buildTypesFile, File productFlavorsFile) {
		buildTypes = new PersistentBooleanMap(buildTypesFile)
		productFlavors = new PersistentBooleanMap(productFlavorsFile)
	}
}