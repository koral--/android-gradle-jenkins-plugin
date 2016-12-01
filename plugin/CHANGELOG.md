### 1.0.31
- chrome added to hidden browser packages

### 1.0.30
- restarting adb as root replaced with `su 0` in commands

### 1.0.29
- browser disabling fixed

### 1.0.28
- `connectedSetupUiTest` fixed

### 1.0.27
- `jenkinsTestable` split into `monkeyTest` and `uiTest` 

### 1.0.26
- ui test task names unification 

### 1.0.25
- no changes, version bumped for gradle plugin portal 

### 1.0.24
- log message instead of throwing an exception if monkey reaches timeout
- added `pl.droidsonroids.jenkins.ui.test.mode` property

### 1.0.23
- monkey task fixed

### 1.0.22
- plugin can be applied to both root projects and subprojects

### 1.0.21
- testable variants check moved to monkey task action to be compatible with instant run

### 1.0.20
- jenkinsTestable moved from expando to dedicated extension

### 1.0.19
- Gradle daemon support fixed for gradle 2.14.1+ - [#1](https://github.com/koral--/android-gradle-jenkins-plugin/issues/1)
- Test only Android projects support added

### 1.0.18
- Gradle version updated to 2.14.1
- Android gradle plugin dependency updated to 2.1.3
- Dependencies versions bump 

### 1.0.17
- Dependencies versions bump
- Predexing library control added

### 1.0.16
- Dependencies versions bump

### 1.0.15
- Android gradle plugin dependency updated to 2.0.0

### 1.0.14
- Monkey hang workaround added
- Monkey timeout reduced to 120s
- Code cleaning

### 1.0.11
- Monkey output cleaning task based on gradle's `Delete`

### 1.0.10
- Android gradle plugin version bumped to 1.5.0

### 1.0.6
- Monkey output appending fixed

### 1.0.5
- Monkey output formatting fixed

### 1.0.4
- Monkey output redirected to monkey.txt

### 1.0.3
- Added jenkinsTestable by default in jenkinsRelease build type

### 1.0.2
- Android gradle plugin version bumped to 1.3.1
- Added connectedMonkeyJenkinsTest task

### 1.0.1
- Android gradle plugin version bumped to 1.3.0
