package pl.droidsonroids.gradle.ci

import com.android.ddmlib.MultiLineReceiver
import java.io.File
import java.io.PrintWriter

class MonkeyOutputReceiver(outputFile: File) : MultiLineReceiver() {
    private val printWriter: PrintWriter = PrintWriter(outputFile.bufferedWriter(bufferSize = 16 shl 10))
    private var isCancelled: Boolean = false

    override fun processNewLines(lines: Array<String>) = lines.forEach { printWriter.println(it) }

    override fun done() = with(printWriter) {
        flush()
        close()
    }

    override fun isCancelled() = isCancelled

    fun cancel() {
        isCancelled = true
    }

}
