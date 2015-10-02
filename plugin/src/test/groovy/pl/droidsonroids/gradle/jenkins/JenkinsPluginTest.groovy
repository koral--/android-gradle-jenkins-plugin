package pl.droidsonroids.gradle.jenkins

import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.ProductFlavor
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

public class JenkinsPluginTest extends BasePluginTest {

    @Test
    void testAddJenkinsTestableBuildType() {
        DefaultBuildType buildType = new DefaultBuildType("test")
        buildType.jenkinsTestable true
        assertThat(buildType.isJenkinsTestable).isTrue()
    }

    @Test
    void testAddJenkinsTestableFlavor() {
        ProductFlavor flavor = new DefaultProductFlavor("test")
        flavor.jenkinsTestable true
        assertThat(flavor.isJenkinsTestable).isTrue()
    }

    @Test
    void testBuildTypeOverriding() {
        android.buildTypes.create('dev', {
            jenkinsTestable true
        })
        android.buildTypes.create('store', {
            jenkinsTestable false
        })
        android.productFlavors.create('staging', {
            jenkinsTestable true
        })
        android.productFlavors.create('production', {
            jenkinsTestable false
        })
        project.evaluate()
        def expectedTestableVariantNames = ['productionDev',
                                            'stagingDebug',
                                            'stagingDev',
                                            'stagingRelease']
        def testableVariantNames = []
        project.tasks.getByName(MonkeyTask.MONKEY_TASK_NAME).applicationVariants.each { testableVariantNames.add it.name }
        assertThat(testableVariantNames).hasSameElementsAs(expectedTestableVariantNames)
    }
}
