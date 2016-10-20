package pl.droidsonroids.gradle.jenkins

import org.junit.Test

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat

public class BaseOutputReceiverTest {
	@Test
	void testIsCancelled() {

		def receiver = new BaseOutputReceiver() {
			@Override
			void processNewLines(String[] lines) {
				//no-op
			}
		}
		assertThat(receiver.isCancelled()).isFalse()
	}
}
