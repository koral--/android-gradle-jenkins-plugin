package pl.droidsonroids.gradle.jenkins

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CleanMonkeyOutputTask extends DefaultTask {
    File monkeyOutputFile

    @TaskAction
    def connectedMonkeyTest() {
        monkeyOutputFile.delete()
    }
}
