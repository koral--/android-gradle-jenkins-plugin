package pl.droidsonroids.gradle.ci

import com.android.ddmlib.MultiLineReceiver

abstract class BaseOutputReceiver : MultiLineReceiver() {
    override fun isCancelled() = false
}
