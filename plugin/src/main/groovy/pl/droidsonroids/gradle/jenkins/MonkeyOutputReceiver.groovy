package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.MultiLineReceiver
import groovy.transform.TupleConstructor
import org.gradle.api.logging.Logger

class MonkeyOutputReceiver extends MultiLineReceiver {
    File file = new File("monkey.txt")

    @Override
    public void processNewLines(String[] lines) {
        lines.each { file<< it }
    }

    @Override
    public boolean isCancelled() {
        false
    }
}