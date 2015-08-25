package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.MultiLineReceiver

abstract class BaseOutputReceiver extends MultiLineReceiver {
    @Override
    boolean isCancelled() {
        false
    }
}