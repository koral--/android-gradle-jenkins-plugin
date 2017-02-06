package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.api.ApplicationVariant

class UiTestExtension {
	String testInstrumentationRunner
	Boolean minifyEnabled

    void minifyEnabled(boolean minifyEnabled) {
		this.minifyEnabled = minifyEnabled
	}

	boolean getDefaultMinifyEnabled(Collection<ApplicationVariant> variants) {
		if (minifyEnabled == null) {
			return variants.find { it.buildType.minifyEnabled } != null
		} else {
			return minifyEnabled
		}
	}

    void testInstrumentationRunner(String testInstrumentationRunner) {
		this.testInstrumentationRunner = testInstrumentationRunner
	}

}
