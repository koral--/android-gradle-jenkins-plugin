package pl.droidsonroids.gradle.jenkins

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat

class MonkeyOutputReceiverTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder()

	MonkeyOutputReceiver receiver
	File file

	@Before
    void setUp() {
		file = temporaryFolder.newFile()
		receiver = new MonkeyOutputReceiver(file)
	}

	@After
    void tearDown() {
		receiver.flush()
	}

	@Test
    void testProcessNewLines() throws Exception {
		String testInput = 'a,b,c,d,e'
		receiver.processNewLines(testInput.split(','))
		receiver.flush()
		assertThat(file).hasContent(testInput.replace(',', '\n'))
	}

	@Test
    void testProcessZeroLines() throws Exception {
		receiver.processNewLines(new String[0])
		receiver.flush()
		assertThat(file).hasContent('')
	}

	@Test
    void testCancel() throws Exception {
		receiver.cancel()
		assertThat(receiver.isCancelled()).isTrue()
	}

	@Test(expected = IOException.class)
    void testNonWritableFile() {
		new MonkeyOutputReceiver(new File(file, "test.txt"))
	}
}