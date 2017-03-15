package pl.droidsonroids.gradle.ci

import com.android.build.gradle.AppExtension

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.SigningConfig
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.testing.api.DeviceConnector
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_SETUP_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.CONNECTED_UI_TEST_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.SPOON_TASK_NAME
import pl.droidsonroids.gradle.ci.Constants.UI_TEST_MODE_PROPERTY_NAME
import java.io.File
import java.io.InputStream

fun Project.addXlintOptionToJavacTasks() = allprojects { subproject ->
    gradle.projectsEvaluated {
        subproject.tasks.withType(JavaCompile::class.java) {
            it.options.compilerArgs.add("-Xlint")
        }
    }
}

inline fun <reified T : BaseExtension> Project.getAndroidExtension() =
        extensions.getByType(T::class.java)!!

fun AppExtension.addJenkinsReleaseBuildType() {
    signingConfigs.add(SigningConfig("jenkinsRelease").apply {
        storeFile = File("${System.getProperty("user.home")}/.android/debug.keystore")
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
    })
}

fun BaseExtension.setDexOptions(disablePredex: Boolean) {
    if (disablePredex) {
        dexOptions.preDexLibraries = false
    }
}

fun Project.addCleanMonkeyOutputTask() {
    val cleanMonkeyOutputTask = tasks.create(Constants.CLEAN_MONKEY_OUTPUT_TASK_NAME, Delete::class.java)
    cleanMonkeyOutputTask.delete(rootProject.fileTree(mapOf("dir" to rootDir, "includes" to listOf("monkey-logcat-*.txt"))))
    tasks.getByName("clean").dependsOn(cleanMonkeyOutputTask)
}

fun Project.addMonkeyTask() {
    val android = getAndroidExtension<AppExtension>()

    val applicationVariants = android.applicationVariants.filter {
        it.isMonkeyTestable(extensions.getByType(MonkeyTestExtension::class.java))
    }

    val monkeyTask = tasks.create(Constants.MONKEY_TASK_NAME, MonkeyTask::class.java, {
        it.appExtension(android)
        it.testableVariants = applicationVariants.toSet()
    })

    applicationVariants.forEach {
        if (it.install == null) {
            throw GradleException("Variant ${it.name} is marked testable but it is not installable. Missing signingConfig?")
        }
        logger.quiet("`${it.name}` build variant is testable by monkey")
        monkeyTask.dependsOn(it.install)
    }
}

val ApplicationVariant.monkeyCommand: String
    get() = "monkey -v --ignore-crashes --ignore-timeouts --ignore-security-exceptions --monitor-native-crashes --ignore-native-crashes -p $applicationId 1000"

val DeviceConnector.logcatFileName: String
    get() = "monkey-logcat-${name.replace(" ", "_")}.txt"

fun Project.logCatFile(device: DeviceConnector) =
        rootProject.file(device.logcatFileName)!!

private fun ApplicationVariant.isMonkeyTestable(monkeyTest: MonkeyTestExtension) =
        monkeyTest.variantNames.contains(name) or
                monkeyTest.buildTypeNames.contains(buildType.name) or
                (productFlavors.find { monkeyTest.productFlavorNames.contains(it.name) } != null)


fun Project.configureUiTests(android: AppExtension, uiTest: UiTestExtension) {
    val uiTestModeName = findProperty(UI_TEST_MODE_PROPERTY_NAME) as String? ?: return

    val deviceSetupTask = tasks.create(CONNECTED_SETUP_UI_TEST_TASK_NAME, DeviceSetupTask::class.java, {
        it.appExtension(android)
    })

    val deviceSetupRevertTask = tasks.create(CONNECTED_SETUP_REVERT_UI_TEST_TASK_NAME, DeviceSetupRevertTask::class.java, {
        it.appExtension(android)
    })

    apply(mapOf("plugin" to "spoon"))

    val spoonTask = tasks.getByName(SPOON_TASK_NAME)
    spoonTask.mustRunAfter(deviceSetupTask)

    tasks.create(CONNECTED_UI_TEST_TASK_NAME) {
        it.group = "verification"
        it.description = "Setups connected devices and performs instrumentation tests"
        it.dependsOn(spoonTask, deviceSetupTask)
        it.finalizedBy(deviceSetupRevertTask)
    }

    val uiTestMode = UiTestMode.valueOf(uiTestModeName)

    android.sourceSets.getByName("androidTest").setRoot(rootProject.file("uiTest").path)
    val variants = android.applicationVariants

    variants.all {
        if (it.buildType.name == android.testBuildType) {
            val defaultMinifyEnabled = uiTest.getDefaultMinifyEnabled(variants)
            val minifyEnabled = uiTestMode.getMinifyEnabled(defaultMinifyEnabled)
            android.buildTypes.getByName(android.testBuildType).isMinifyEnabled = minifyEnabled
            logger.quiet("minifyEnabled for ${it.buildType.name} set to $minifyEnabled")
        }
        (it.mergedFlavor as DefaultProductFlavor).testInstrumentationRunner = uiTest.testInstrumentationRunner
        logger.quiet("Instrumentation test runner for ${it.name}: ${uiTest.testInstrumentationRunner}")
    }
}

fun InputStream.toFile(file: File) {
    use { input ->
        file.outputStream().use { input.copyTo(it) }
    }
}
