package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor
import com.android.ddmlib.DdmPreferences
import org.gradle.api.*
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.util.GradleVersion

public class JenkinsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (GradleVersion.current() < GradleVersion.version('2.6')) {
            throw new GradleException("Gradle Version ${GradleVersion.current()} not supported. Use Gradle Wrapper or with Gradle version >= 2.6")
        }

        project.pluginManager.apply(BasePlugin)
        DdmPreferences.setTimeOut(30000)
        addJenkinsTestableDSL()
        addJavacXlint(project)

        project.allprojects { Project subproject ->
            subproject.plugins.withType(AppPlugin) {
                addJenkinsReleaseBuildType(subproject)
                subproject.afterEvaluate {
                    addMonkeyTask(subproject)
                }
            }
        }
        addCleanMonkeyOutputTask(project)
    }

    def addCleanMonkeyOutputTask(Project project) {
        def cleanMonkeyOutput = project.tasks.create('cleanMonkeyOutput', new Action<Task>() {
            @Override
            void execute(Task task) {
                project.rootProject.fileTree(dir: project.rootDir, includes: ['monkey*']).each {
                    it.delete()
                }
            }
        })
        project.clean.dependsOn cleanMonkeyOutput
    }

    static def addJenkinsTestableDSL() {
        DefaultProductFlavor.metaClass.isJenkinsTestable = null
        DefaultBuildType.metaClass.isJenkinsTestable = null
        ProductFlavor.metaClass.jenkinsTestable { boolean isJenkinsTestable ->
            delegate.isJenkinsTestable = isJenkinsTestable
        }
        BuildType.metaClass.jenkinsTestable { boolean isJenkinsTestable ->
            delegate.isJenkinsTestable = isJenkinsTestable
        }
    }

    def addMonkeyTask(Project project) {
        def android = project.extensions.getByType(AppExtension)
        def applicationVariants = android.applicationVariants.findAll {
            if (it.buildType.isJenkinsTestable != null) {
                return it.buildType.isJenkinsTestable
            }
            for (ProductFlavor flavor : it.productFlavors) {
                if (flavor.isJenkinsTestable != null) {
                    return flavor.isJenkinsTestable
                }
            }
            false
        }
        if (applicationVariants.isEmpty()) {
            throw new GradleException('No jenkins testable application variants found')
        }
        def monkeyTask = project.tasks.create(MonkeyTask.MONKEY_TASK_NAME, MonkeyTask, {
            it.init(applicationVariants)
        })
        applicationVariants.each {
            if (it.install == null) {
                throw new GradleException("Variant ${it.name} is marked testable but it is not installable. Missing singningConfig?")
            }
            monkeyTask.dependsOn it.install
        }
    }

    def addJenkinsReleaseBuildType(def subproject) {
        def android = subproject.extensions.getByType(AppExtension)
        android.signingConfigs {
            jenkinsRelease {
                storeFile new File("$System.env.HOME/.android/debug.keystore")
                storePassword 'android'
                keyAlias 'androiddebugkey'
                keyPassword 'android'
            }
        }
    }

    def addJavacXlint(Project project) {
        project.allprojects {
            gradle.projectsEvaluated {
                tasks.withType(JavaCompile) {
                    options.compilerArgs << '-Xlint'
                }
            }
        }
    }
}