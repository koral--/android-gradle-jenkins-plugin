package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.MultiLineReceiver

abstract class BaseOutputReceiver : MultiLineReceiver() {
    override fun isCancelled() = false
}
