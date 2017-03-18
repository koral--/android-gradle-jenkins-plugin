package pl.droidsonroids.gradle.ci

import com.android.build.gradle.api.ApplicationVariant

open class UiTestExtension {
    var testInstrumentationRunner: String? = null
    var minifyEnabled: Boolean? = null

    fun minifyEnabled(minifyEnabled: Boolean) {
        this.minifyEnabled = minifyEnabled
    }

    fun getDefaultMinifyEnabled(variants: Collection<ApplicationVariant>) =
            minifyEnabled.let { it } ?: variants.find { it.buildType.isMinifyEnabled } != null

    fun testInstrumentationRunner(testInstrumentationRunner: String) {
        this.testInstrumentationRunner = testInstrumentationRunner
    }
}
