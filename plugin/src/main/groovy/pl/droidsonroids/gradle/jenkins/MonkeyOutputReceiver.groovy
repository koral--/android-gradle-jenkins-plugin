package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.MultiLineReceiver
import groovy.transform.TupleConstructor
import org.gradle.api.logging.Logger

@TupleConstructor
class MonkeyOutputReceiver extends MultiLineReceiver {
    Logger logger

    @Override
    public void processNewLines(String[] lines) {
        lines.each { logger.lifecycle it }
    }

    @Override
    public boolean isCancelled() {
        false
    }
}