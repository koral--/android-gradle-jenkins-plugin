package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

public class JenkinsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.allprojects {
            gradle.projectsEvaluated {
                tasks.withType(JavaCompile) {
                    options.compilerArgs << "-Xlint"
                }
            }
        }
        project.allprojects { subproject ->
            subproject.plugins.withType(AppPlugin) {
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
                    }
                }
            }

        }
    }
}