package pl.droidsonroids.gradle.jenkins

import com.android.utils.ILogger
import groovy.transform.TupleConstructor

@TupleConstructor
class LoggingOutputReceiver(val logger:ILogger): BaseOutputReceiver() {
	override fun processNewLines(lines: Array<out String>) = lines.forEach { logger::info }
}