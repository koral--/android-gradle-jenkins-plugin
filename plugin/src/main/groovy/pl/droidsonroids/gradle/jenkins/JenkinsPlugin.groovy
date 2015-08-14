package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.LoggerWrapper
import com.android.builder.testing.ConnectedDevice
import com.android.builder.testing.ConnectedDeviceProvider
import com.android.ddmlib.DdmPreferences
import com.android.ddmlib.MultiLineReceiver
import com.android.utils.StdLogger
import groovy.transform.TupleConstructor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.compile.JavaCompile

import java.util.concurrent.TimeUnit

public class JenkinsPlugin implements Plugin<Project> {

    @TupleConstructor
    static class MonkeyOutputReceiver extends MultiLineReceiver {
        Logger logger

        @Override
        public void processNewLines(String[] lines) {
            lines.each { logger.lifecycle it }
        }

        @Override
        public boolean isCancelled() {
            false
        }
    }

    @Override
    void apply(Project project) {

        DdmPreferences.setTimeOut(30000)
        addJavacXlint(project)

        project.allprojects { Project subproject ->
            subproject.plugins.withType(AppPlugin) {
                addJenkinsReleaseBuildType(subproject)
                subproject.task(group: 'verification',
                        description: 'Runs monkey application exerciser on all connected devices and/or emulators',
                        action: {
                            def android = subproject.extensions.getByType(AppExtension)
                            def connectedDeviceProvider = new ConnectedDeviceProvider(android.adbExe,
                                    new LoggerWrapper(subproject.logger))
                            //TODO
                            def variant = android.applicationVariants.getAt(0)
                            def command = 'monkey -p ' + variant.applicationId + ' 1000'
                            def receiver = new MonkeyOutputReceiver(subproject.logger)
                            connectedDeviceProvider.init()
                            connectedDeviceProvider.getDevices().findAll {
                                it.apiLevel >= variant.getMergedFlavor().minSdkVersion
                            }.each { device ->
                                device.executeShellCommand(command, receiver, 5, TimeUnit.SECONDS)
                            }
                            connectedDeviceProvider.terminate()
                        },
                        'connectedMonkeyTest')
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