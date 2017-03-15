package pl.droidsonroids.gradle.ci

import org.junit.rules.TemporaryFolder
import java.io.File

class TemporaryProjectFolder : TemporaryFolder() {

    fun copyResource(resourceName: String, fileName: String) =
            javaClass.classLoader.getResourceAsStream(resourceName).toFile(projectFile(fileName))

    fun projectFile(fileName: String) = File(root, fileName)
}