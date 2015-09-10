package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor
import com.android.ddmlib.DdmPreferences
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

public class JenkinsPlugin implements Plugin<Project> {

    static def addDSL() {
        DefaultProductFlavor.metaClass.isJenkinsTestable = null
        DefaultBuildType.metaClass.isJenkinsTestable = null
        ProductFlavor.metaClass.jenkinsTestable { boolean isJenkinsTestable ->
            delegate.isJenkinsTestable = isJenkinsTestable
        }
        BuildType.metaClass.jenkinsTestable { boolean isJenkinsTestable ->
            delegate.isJenkinsTestable = isJenkinsTestable
        }
    }

    static def addMonkeyTask(Project subproject) {
        def android = subproject.extensions.getByType(AppExtension)
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
            throw new GradleException("No jenkins testable application variants found")
        }
        File monkeyOutputFile = subproject.getRootProject().file('monkey.txt')
        def monkeyTask = subproject.tasks.create('connectedMonkeyJenkinsTest', MonkeyTask, {
            it.subproject = subproject
            it.applicationVariants = applicationVariants
            it.monkeyOutputFile = monkeyOutputFile
        })
        def cleanMonkeyOutputTask = subproject.tasks.create('cleanMonkeyOutput', CleanMonkeyOutput, {
            it.monkeyOutputFile = monkeyOutputFile
        })
        applicationVariants.each { cleanMonkeyOutputTask.dependsOn it.clean }
        applicationVariants.each { monkeyTask.dependsOn it.install }
    }

    @Override
    void apply(Project project) {

        DdmPreferences.setTimeOut(30000)
        addDSL()
        addJavacXlint(project)

        project.allprojects { Project subproject ->
            subproject.plugins.withType(AppPlugin) {
                addJenkinsReleaseBuildType(subproject)
                subproject.afterEvaluate {
                    addMonkeyTask(subproject)
                }
            }
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
        android.buildTypes {
            jenkinsRelease {
                minifyEnabled true
                proguardFiles android.getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                signingConfig android.signingConfigs.jenkinsRelease
                jenkinsTestable true
            }
        }
    }

    def addJavacXlint(Project project) {
        project.allprojects {
            gradle.projectsEvaluated {
                tasks.withType(JavaCompile) {
                    options.compilerArgs << "-Xlint"
                }
            }
        }
    }
}