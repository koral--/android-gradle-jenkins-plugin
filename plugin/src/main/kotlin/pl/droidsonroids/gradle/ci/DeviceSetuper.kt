package pl.droidsonroids.gradle.ci

import com.android.ddmlib.IDevice
import java.io.File

class DeviceSetuper : DeviceWorker() {

    val tempDir = createTempDir()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { cleanTempDirectory() })
    }

    fun cleanTempDirectory() = tempDir.deleteRecursively()

    override fun doWork(device: IDevice) {
        if (device.version.isGreaterOrEqualThan(17)) {
            device.executeRemoteCommand("settings put global window_animation_scale 0")
            device.executeRemoteCommand("settings put global transition_animation_scale 0")
            device.executeRemoteCommand("settings put global animator_duration_scale 0")
        } else {
            logger.verbose("Animation disabling skipped on %s, API level %d < 17", device.name, device.version.apiLevel)
        }

        arrayOf("image_portrait.jpg", "image_square.jpg", "video.mp4").forEach { name ->
            val file = pushFile(device, name, "/sdcard/")
            device.executeRemoteCommand("${Constants.MEDIA_SCAN_COMMAND}$file")
        }
        device.executeRemoteCommand("su 0 pm disable com.android.browser")
        device.executeRemoteCommand("su 0 pm hide org.chromium.webview_shell")
        device.executeRemoteCommand("su 0 pm hide com.android.chrome")

        if (device.version.isGreaterOrEqualThan(23)) {
            device.executeRemoteCommand("wm dismiss-keyguard")
        } else {
            device.executeRemoteCommand("input keyevent 82")
            device.executeRemoteCommand("input swipe 0 200 0 0")
            device.executeRemoteCommand("input swipe 0 500 0 0")
        }
    }

    fun pushFile(device: IDevice, fileName: String, remotePath: String): String {
        val file = File(tempDir, fileName)
        if (!file.isFile) {
            javaClass.getResourceAsStream(fileName).toFile(file)
        }

        val remoteFilePath = remotePath + fileName
        device.pushFile(file.path, remoteFilePath)
        return remoteFilePath
    }

}