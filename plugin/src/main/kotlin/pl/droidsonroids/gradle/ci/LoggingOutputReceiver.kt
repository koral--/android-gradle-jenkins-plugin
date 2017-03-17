package pl.droidsonroids.gradle.ci

import com.android.utils.ILogger

class LoggingOutputReceiver(val logger: ILogger) : BaseOutputReceiver() {
    override fun processNewLines(lines: Array<String>) = lines.forEach { logger.info(it) }
}