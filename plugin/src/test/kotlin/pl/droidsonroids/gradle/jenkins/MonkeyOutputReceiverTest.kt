package pl.droidsonroids.gradle.jenkins

import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException

class MonkeyOutputReceiverTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()
    private lateinit var receiver: MonkeyOutputReceiver
    private lateinit var file: File
    
    @Before
    fun setUp() {
        file = temporaryFolder.newFile()
        receiver = MonkeyOutputReceiver(file)
    }

    @After
    fun tearDown() {
        receiver.flush()
    }

    @Test
    @Throws(Exception::class)
    fun `non-empty lines processed`() {
        val testInput = "a,b,c,d,e"
        receiver.processNewLines(testInput.split(",".toRegex()).dropLastWhile(String::isEmpty).toTypedArray())
        receiver.flush()
        assertThat(file).hasContent(testInput.replace(",", "\n"))
    }

    @Test
    fun `empty lines processed`() {
        receiver.processNewLines(emptyArray())
        receiver.flush()
        assertThat(file).hasContent("")
    }

    @Test
    fun `cancel request handled`() {
        receiver.cancel()
        assertThat(receiver.isCancelled).isTrue()
    }

    @Test(expected = IOException::class)
    fun `exception thrown on non-writable file`() {
        MonkeyOutputReceiver(File(file, "test.txt"))
    }
}
