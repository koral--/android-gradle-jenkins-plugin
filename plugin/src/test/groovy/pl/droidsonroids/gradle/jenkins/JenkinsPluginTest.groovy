package pl.droidsonroids.gradle.jenkins

import com.android.builder.core.DefaultBuildType
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*

/**
 * Created by koral on 02.09.15.
 */
class JenkinsPluginTest {
    @Test
    void "is jenkinsTestable added to buildType"() {

        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.android.application'
        project.pluginManager.apply JenkinsPlugin.class

        DefaultBuildType buildType = new DefaultBuildType("test")
        buildType.jenkinsTestable true
        assertTrue(buildType.isJenkinsTestable)

    }
}
