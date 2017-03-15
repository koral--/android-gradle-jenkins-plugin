package pl.droidsonroids.gradle.ci

import org.gradle.api.tasks.Internal

open class DeviceSetupTask : DeviceActionTask() {
    init {
        description = "Setups device before instrumentation tests"
    }

    @Internal
    override val worker = DeviceSetuper()
}
