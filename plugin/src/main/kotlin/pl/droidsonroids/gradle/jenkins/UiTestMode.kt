package pl.droidsonroids.gradle.jenkins

enum class UiTestMode {
    noMinify, minify;

    fun getMinifyEnabled(defaultMinifyEnabled: Boolean) = when (this) {
        noMinify -> false
        else -> defaultMinifyEnabled
    }
}
