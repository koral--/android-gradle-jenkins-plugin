package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.MultiLineReceiver

class MonkeyOutputReceiver extends MultiLineReceiver {

    PrintWriter printWriter
    MonkeyOutputReceiver() throws IOException {
        printWriter = new PrintWriter(new File('monkey.txt'))
    }

    @Override
    public void processNewLines(String[] lines) {
        lines.each { printWriter.println(it) }
    }

    @Override
    public void done() {
        printWriter.close()
    }

    @Override
    public boolean isCancelled() {
        false
    }
}