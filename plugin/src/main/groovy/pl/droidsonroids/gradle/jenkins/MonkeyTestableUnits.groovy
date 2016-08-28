package pl.droidsonroids.gradle.jenkins

import com.jamierf.persistenthashmap.PersistentHashMap

class MonkeyTestableUnits {
	final Map<String, Boolean> buildTypes
	final Map<String, Boolean> productFlavors

	MonkeyTestableUnits(File buildTypesDir, File productFlavorsDir) {
		buildTypes = new PersistentHashMap<String, Boolean>(buildTypesDir)
		productFlavors = new PersistentHashMap<String, Boolean>(productFlavorsDir)
	}
}