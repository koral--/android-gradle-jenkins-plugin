package pl.droidsonroids.gradle.jenkins

enum UiTestMode {
	noMinify, minify

	boolean getMinifyEnabled(boolean defaultMinifyEnabled) {
		return this == noMinify ? false : defaultMinifyEnabled
	}
}