package pl.droidsonroids.gradle.jenkins

import org.gradle.api.tasks.TaskAction

class CleanMonkeyOutput {
    File monkeyOutputFile

    @TaskAction
    def connectedMonkeyTest() {
        monkeyOutputFile.delete()
    }
}
