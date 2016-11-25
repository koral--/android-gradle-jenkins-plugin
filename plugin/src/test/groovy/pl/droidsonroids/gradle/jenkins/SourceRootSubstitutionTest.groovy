package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.api.AndroidSourceSet
import org.assertj.core.api.JUnitSoftAssertions
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test

public class SourceRootSubstitutionTest {

	@Rule
	public JUnitSoftAssertions softly = new JUnitSoftAssertions()

	@Test
	public void testAndroidSourceRootSubstitution() throws Exception {
		def project = ProjectBuilder.builder().build()
		project.ext."${Constants.UI_TEST_MODE_PROPERTY_NAME}" = UiTestMode.minify.name()

		project.pluginManager.apply('pl.droidsonroids.jenkins')
		project.pluginManager.apply('com.android.application')

		project.android {
			defaultConfig {
				applicationId 'pl.droidsonroids.testapplication'
				signingConfig signingConfigs.jenkinsRelease
				minSdkVersion 1
			}
			buildToolsVersion '25.0.0'
			compileSdkVersion 25
		}
		AndroidSourceSet androidTest = project.android.sourceSets.androidTest

		def baseDir = project.rootProject.file('uiTest').path

		androidTest.with {
			def sets = [java.srcDirs, resources.srcDirs, res.srcDirs, aidl.srcDirs,
			            renderscript.srcDirs, jni.srcDirs, jniLibs.srcDirs, shaders.srcDirs]
			for (Set<File> srcDirs : sets) {
				softly.assertThat(srcDirs).hasSize(1)
				softly.assertThat(srcDirs.first().path).startsWith(baseDir)
			}
			softly.assertThat(manifest.srcFile.path).startsWith(baseDir)
		}
	}
}