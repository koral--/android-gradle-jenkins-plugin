package pl.droidsonroids.gradle.jenkins

import org.assertj.core.api.SoftAssertions
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class TestableVariantsFunctionalTest {

	private static final String VARIANT_LINE_SUFFIX = 'build variant is testable by monkey'
	@Rule
	public TemporaryProjectFolder mTemporaryFolder = new TemporaryProjectFolder()

	@Test
	void testApplicationVariants() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('variant.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withProjectDir(mTemporaryFolder.root)
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'productionDev', 'stagingDebug')
	}

	@Test
	void testBuildTypesAndProductFlavors() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('mix.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withProjectDir(mTemporaryFolder.root)
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'productionDev', 'stagingDebug', 'stagingDev', 'stagingRelease', 'stagingStore')
	}

	@Test
	void testBuildTypesAndProductFlavorsDomainObjects() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('mixDomainObjects.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withProjectDir(mTemporaryFolder.root)
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'productionDev', 'stagingDebug', 'stagingDev', 'stagingRelease', 'stagingStore')
	}

	@Test
	public void testAddJenkinsTestableBuildType() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'debug')
	}

	@Test
	void testAddJenkinsTestableFlavor() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('productFlavor.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'proDebug', 'proRelease')
	}

	@Test
	public void testNoTestableVariant() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('noTestableVariant.gradle', 'build.gradle')
		GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('connectedMonkeyJenkinsTest')
				.withPluginClasspath()
				.buildAndFail()
	}

	@Test
	public void testNoSigningConfig() {
		mTemporaryFolder.copyResource('base.gradle', 'base.gradle')
		mTemporaryFolder.copyResource('noSigningConfig.gradle', 'build.gradle')
		GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.buildAndFail()
	}

	private static void assertTestableVariants(BuildResult result, String... expectedVariants) {
		assertThat(result.output.readLines().findAll { it.endsWith(VARIANT_LINE_SUFFIX) }).hasSize(expectedVariants.size())
		def softAssertions = new SoftAssertions()
		expectedVariants.each {
			softAssertions.assertThat(result.output).contains("`$it` build variant is testable by monkey").as(it)
		}
		softAssertions.assertAll()
	}
}