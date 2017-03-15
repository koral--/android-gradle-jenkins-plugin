package pl.droidsonroids.gradle.jenkins

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

class TestableVariantsFunctionalTest {
    val VARIANT_LINE_SUFFIX = "build variant is testable by monkey"
    @get:Rule
    val temporaryFolder = TemporaryProjectFolder()

    @Test
    fun testApplicationVariants() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("variant.gradle", "build.gradle")
        val result = GradleRunner.create()
                .withTestKitDir(temporaryFolder.newFolder())
                .withProjectDir(temporaryFolder.root)
                .withArguments("projects")
                .withPluginClasspath()
                .build()
        assertTestableVariants(result, "productionDev", "stagingDebug")
    }

    @Test
    fun testBuildTypesAndProductFlavors() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("mix.gradle", "build.gradle")
        val result = GradleRunner.create()
                .withTestKitDir(temporaryFolder.newFolder())
                .withProjectDir(temporaryFolder.root)
                .withArguments("projects")
                .withPluginClasspath()
                .build()
        assertTestableVariants(result, "productionDev", "stagingDebug", "stagingDev", "stagingRelease", "stagingStore")
    }

    @Test
    fun testBuildTypesAndProductFlavorsDomainObjects() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("mixDomainObjects.gradle", "build.gradle")
        val result = GradleRunner.create()
                .withTestKitDir(temporaryFolder.newFolder())
                .withProjectDir(temporaryFolder.root)
                .withArguments("projects")
                .withPluginClasspath()
                .build()
        assertTestableVariants(result, "productionDev", "stagingDebug", "stagingDev", "stagingRelease", "stagingStore")
    }

    @Test
    fun testAddMonkeyTestBuildType() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("buildType.gradle", "build.gradle")
        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments("projects")
                .withPluginClasspath()
                .build()
        assertTestableVariants(result, "debug")
    }

    @Test
    fun testAddMonkeyTestableFlavor() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("productFlavor.gradle", "build.gradle")
        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments("projects")
                .withPluginClasspath()
                .build()
        assertTestableVariants(result, "proDebug", "proRelease")
    }

    @Test
    fun testNoTestableVariant() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("noTestableVariant.gradle", "build.gradle")
        GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments("connectedMonkeyJenkinsTest")
                .withPluginClasspath()
                .buildAndFail()
    }

    @Test
    fun testNoSigningConfig() {
        temporaryFolder.copyResource("base.gradle", "base.gradle")
        temporaryFolder.copyResource("noSigningConfig.gradle", "build.gradle")
        GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments("projects")
                .withPluginClasspath()
                .buildAndFail()
    }

    private fun assertTestableVariants(result: BuildResult, vararg expectedVariants: String) {
        assertThat(result.output.lines().filter { it.endsWith(VARIANT_LINE_SUFFIX) }).hasSize(expectedVariants.size)
        val softAssertions = SoftAssertions()
        expectedVariants.forEach {
            softAssertions.assertThat(result.output).contains("`$it` build variant is testable by monkey")
        }
        softAssertions.assertAll()
    }
}