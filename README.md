# android-gradle-jenkins-plugin
Gradle plugin for CI Android projects

## Current version
See [Gradle plugin portal](https://plugins.gradle.org/plugin/pl.droidsonroids.jenkins)

## Overview
- `-Xlint` option added to javac parameters, which causes all javac warnings to be included in output.
- `jenkinsRelease` signing config added (using default debug keystore), obfuscated and optimized application can be
 installed and tested.
- ADB connection timeout increased to 30s
- Ability to disable pre dexing (if appropriate property is set), useful on CI servers.
 [More info](http://www.littlerobots.nl/blog/disable-android-pre-dexing-on-ci-builds/).
- Gradle task `connectedMonkeyJenkinsTest` which performs installation and monkeying of all monkey testable variants
 of all applications on all compatible connected devices
- Preparation of connected device/AVD to instrumented (eg. using espresso) tests - unlocking screen, disabling animations,
 adding sample media files to multimedia provider

##Usage
###Sample overview
```groovy
plugins {
  id 'pl.droidsonroids.jenkins' version '1.0.43'
}

apply plugin: 'com.android.application'
//or apply plugin: 'com.android.library'
//or apply plugin: 'com.android.test'

android {
	buildTypes {
		release {
			signingConfig signingConfigs.jenkinsRelease //or another config
		}
	}
}

monkeyTest {
	buildTypes 'release'
	//and/or productFlavors ...
	//and/or applicationVariants ...
}
```

###Project types support
This plugin supports both application, library and test projects. Signing config and monkey task are applicable only for
application projects.

##Features
###`-Xlint` javac option
If project uses `javac` compiler (contains Java source code and jack is not enabled) then
[`-Xlint` option](http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javac.html#nonstandard) is added.
As a result all warnings are printed to console and can be visualised eg. using Jenkins plugins like
[Warnings](https://wiki.jenkins-ci.org/display/JENKINS/Warnings+Plugin).

###Signing config
`jenkinsRelease` signing config is available for use. It assumes that default debug keystore (`$HOME/.android/debug.keystore`)
is usable and has unchanged default credentials.

###ADB connection timeout
Timeout is increased from default 5 s to 30 s.

###`connectedMonkeyJenkinsTest` task
Task is added to application projects. All build variants evaluated from `monkeyTest` extension are monkeyed.
Task will fail if there is no monkeyable variants or at least one of them is not installable (eg. does not have signing
config).

###`monkeyTest` stanza
Monkeyable build types and/or product flavors (both of them by name eg. `debug` or reference `android.buildTypes.debug`)
and/or application variants (by name) can be specified here.