package pl.droidsonroids.gradle.jenkins

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static pl.droidsonroids.gradle.jenkins.UiTestMode.minify
import static pl.droidsonroids.gradle.jenkins.UiTestMode.noMinify

public class UiTestModeTest {

	@Test
	public void testNoMinifyGetMinifyEnabled() {
		assertThat(noMinify.getMinifyEnabled(true)).isFalse()
		assertThat(noMinify.getMinifyEnabled(false)).isFalse()
	}

	@Test
	public void testMinifyGetMinifyEnabled() {
		assertThat(minify.getMinifyEnabled(true)).isTrue()
		assertThat(minify.getMinifyEnabled(false)).isFalse()
	}
}
