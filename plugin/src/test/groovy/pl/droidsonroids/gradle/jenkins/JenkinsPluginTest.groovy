package pl.droidsonroids.gradle.jenkins

import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.ProductFlavor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class JenkinsPluginTest {
    Project project

    @Before void setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.android.application'
        project.pluginManager.apply JenkinsPlugin.class
    }

    @Test
    void testAddJenkinsTestableBuildType() {
        DefaultBuildType buildType = new DefaultBuildType("test")
        buildType.jenkinsTestable true
        assertTrue(buildType.isJenkinsTestable)
    }

    @Test
    void testAddJenkinsTestableFlavor() {
        ProductFlavor flavor = new DefaultProductFlavor("test")
        flavor.jenkinsTestable true
        assertTrue(flavor.isJenkinsTestable)
    }
}
