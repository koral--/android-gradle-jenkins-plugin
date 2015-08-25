package pl.droidsonroids.gradle.jenkins

import com.android.utils.ILogger
import groovy.transform.TupleConstructor

@TupleConstructor
class AppUninstaller extends BaseOutputReceiver {

    def device
    ILogger logger

    @Override
    void processNewLines(String[] lines) {
        lines.findAll {
            it.startsWith('package:')
        }.collect {
            it.split(':', -1)[1]
        }.each {
            device.uninstallPackage(it, Cleaner.ADB_COMMAND_TIMEOUT_SECONDS, logger)
        }
    }
}