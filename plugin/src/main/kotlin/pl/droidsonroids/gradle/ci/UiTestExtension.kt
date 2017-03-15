package pl.droidsonroids.gradle.ci

import com.android.build.gradle.api.ApplicationVariant

open class UiTestExtension {
    fun minifyEnabled(minifyEnabled: Boolean) {
        this.minifyEnabled = minifyEnabled
    }

    fun getDefaultMinifyEnabled(variants: Collection<ApplicationVariant>): Boolean =
            if (minifyEnabled == null) {
                variants.find { it.buildType.isMinifyEnabled } != null
            } else {
                minifyEnabled!!
            }

    fun testInstrumentationRunner(testInstrumentationRunner: String) {
        this.testInstrumentationRunner = testInstrumentationRunner
    }

    var testInstrumentationRunner: String? = null
    var minifyEnabled: Boolean? = null
}
