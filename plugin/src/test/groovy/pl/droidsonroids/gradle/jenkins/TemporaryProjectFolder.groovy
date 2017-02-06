package pl.droidsonroids.gradle.jenkins

import com.google.common.io.Resources
import org.junit.rules.TemporaryFolder

class TemporaryProjectFolder extends TemporaryFolder {

	void copyResource(String resourceName, String fileName) {
		projectFile(fileName).withOutputStream {
			Resources.copy(getClass().classLoader.getResource(resourceName), it)
		}
	}

    File projectFile(String fileName) {
		new File(root, fileName)
	}
}