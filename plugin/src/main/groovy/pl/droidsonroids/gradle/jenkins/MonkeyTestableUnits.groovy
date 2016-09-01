package pl.droidsonroids.gradle.jenkins

import org.gradle.api.Project

class MonkeyTestableUnits {
	enum Kind {
		BUILD_TYPE, PRODUCT_FLAVOR
	}
	private final Map<String, Boolean> buildTypes
	private final Map<String, Boolean> productFlavors

	MonkeyTestableUnits(Project project) {
		def buildTypesFile = new File(project.buildDir, 'jenkinsTestableBuildTypes.properties')
		def productFlavorsFile = new File(project.buildDir, 'jenkinsTestableProductFlavors.properties')
		project.gradle.buildFinished {
			buildTypesFile.delete()
			productFlavorsFile.delete()
		}
		buildTypes = new PersistentBooleanMap(buildTypesFile)
		productFlavors = new PersistentBooleanMap(productFlavorsFile)
	}

	private Map<String, Boolean> getMap(Kind kind) {
		switch (kind) {
			case Kind.BUILD_TYPE:
				return buildTypes
			case Kind.PRODUCT_FLAVOR:
				return productFlavors
		}
	}

	boolean isTestable(String name, Kind kind) {
		getMap(kind).get(name)
	}

	boolean contains(String name, Kind kind) {
		getMap(kind).containsKey(name)
	}

	void setTestable(String name, Kind kind, boolean testable) {
		getMap(kind).put(name, testable)
	}
}