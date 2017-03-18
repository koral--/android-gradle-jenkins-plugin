package pl.droidsonroids.ci

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SetupJacocoTestkitProperties : DefaultTask() {
    @OutputFile
    val outputFile: File = project.file("${project.buildDir}/testkit/gradle.properties")

    @TaskAction
    fun createJacocoProperties() {
        outputFile.parentFile.mkdirs()
        val jacocoRuntimePath = project.configurations.getByName("jacocoRuntime").asPath
        val destFile = "${project.buildDir}/jacoco/testKit.exec"
        outputFile.writeText("org.gradle.jvmargs:-javaagent:$jacocoRuntimePath=destfile=$destFile")
    }
}