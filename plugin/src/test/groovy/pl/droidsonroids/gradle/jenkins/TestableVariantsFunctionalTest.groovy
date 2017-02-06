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
	public TemporaryProjectFolder temporaryFolder = new TemporaryProjectFolder()

	@Test
	void testApplicationVariants() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('variant.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withTestKitDir(temporaryFolder.newFolder())
				.withProjectDir(temporaryFolder.root)
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'productionDev', 'stagingDebug')
	}

	@Test
	void testBuildTypesAndProductFlavors() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('mix.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withTestKitDir(temporaryFolder.newFolder())
				.withProjectDir(temporaryFolder.root)
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'productionDev', 'stagingDebug', 'stagingDev', 'stagingRelease', 'stagingStore')
	}

	@Test
	void testBuildTypesAndProductFlavorsDomainObjects() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('mixDomainObjects.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withTestKitDir(temporaryFolder.newFolder())
				.withProjectDir(temporaryFolder.root)
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'productionDev', 'stagingDebug', 'stagingDev', 'stagingRelease', 'stagingStore')
	}

	@Test
    void testAddMonkeyTestBuildType() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('buildType.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'debug')
	}

	@Test
	void testAddMonkeyTestableFlavor() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('productFlavor.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'proDebug', 'proRelease')
	}

	@Test
    void testNoTestableVariant() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('noTestableVariant.gradle', 'build.gradle')
		GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
				.withArguments('connectedMonkeyJenkinsTest')
				.withPluginClasspath()
				.buildAndFail()
	}

	@Test
    void testNoSigningConfig() {
		temporaryFolder.copyResource('base.gradle', 'base.gradle')
		temporaryFolder.copyResource('noSigningConfig.gradle', 'build.gradle')
		GradleRunner.create()
				.withProjectDir(temporaryFolder.root)
				.withTestKitDir(temporaryFolder.newFolder())
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