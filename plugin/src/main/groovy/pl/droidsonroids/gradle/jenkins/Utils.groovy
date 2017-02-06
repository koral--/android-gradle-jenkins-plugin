package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

static addJavacXlint(Project project) {
	project.allprojects { Project subproject ->
		gradle.projectsEvaluated {
			subproject.tasks.withType(JavaCompile) {
				options.compilerArgs << '-Xlint'
			}
		}
	}
}

static addJenkinsReleaseBuildType(AppExtension android) {
	android.signingConfigs {
		jenkinsRelease {
			storeFile new File("$System.env.HOME/.android/debug.keystore")
			storePassword 'android'
			keyAlias 'androiddebugkey'
			keyPassword 'android'
		}
	}
}

static setDexOptions(BaseExtension android, boolean disablePredex) {
	if (disablePredex) {
		android.dexOptions.setPreDexLibraries(false)
	}
}
