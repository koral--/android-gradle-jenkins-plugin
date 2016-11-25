package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.builder.model.ProductFlavor
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Delete

static def addMonkeyTask(Project project, AppExtension android) {
	def jenkinsTestable = project.extensions.getByType(TestableExtension)

	def applicationVariants = android.applicationVariants.findAll {
		if (jenkinsTestable.variantNames.contains(it.name)) {
			return true
		}
		if (jenkinsTestable.buildTypeNames.contains(it.buildType.name)) {
			return true
		}
		for (ProductFlavor flavor : it.productFlavors) {
			if (jenkinsTestable.productFlavorNames.contains(flavor.name)) {
				return true
			}
		}
		false
	}

	def monkeyTask = project.tasks.create(Constants.MONKEY_TASK_NAME, MonkeyTask, {
		appExtension android
	})
	applicationVariants.each {
		if (it.install == null) {
			throw new GradleException("Variant ${it.name} is marked testable but it is not installable. Missing singningConfig?")
		}
		project.logger.quiet("`$it.name` build variant is testable by monkey")
		monkeyTask.dependsOn it.install
	}
}

static def addCleanMonkeyOutputTask(Project project) {
	def cleanMonkeyOutput = project.tasks.create(Constants.CLEAN_MONKEY_OUTPUT_TASK_NAME, Delete)
	cleanMonkeyOutput.delete project.rootProject.fileTree(dir: project.rootDir, includes: ['monkey-logcat-*.txt'])
	project.clean.dependsOn cleanMonkeyOutput
}
