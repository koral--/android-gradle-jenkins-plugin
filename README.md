# android-gradle-jenkins-plugin
Gradle plugin for CI Android projects on Jenkins

##Features
- Adding "-Xlint" to javac parameters, which causes all javac warnings to be included in output. Plugins like [Warnings](https://wiki.jenkins-ci.org/display/JENKINS/Warnings+Plugin) can be used to visualize it.
- Adding `jenkinsRelease` signing config (using default debug keystore), so jenkins can build, install and test obfuscated application
- Increasing ADB connection timeout to 30s
- Adding new DSL method `jenkinsTestable` to build variant and flavor (`jenkinsRelease` has it set to `true`)
- Adding new task `connectedMonkeyJenkinsTest` which performs installation and monkeying of all `jenkinsTestable` variants of all applications on all compatible connected devices 

## Usage
See [Gradle plugin portal](https://plugins.gradle.org/plugin/pl.droidsonroids.jenkins)
