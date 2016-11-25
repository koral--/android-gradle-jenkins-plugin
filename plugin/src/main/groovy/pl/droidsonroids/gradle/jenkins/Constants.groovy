package pl.droidsonroids.gradle.jenkins

class Constants {

	static final long MONKEY_RUN_TIMEOUT_MILLIS = 180_000
	static final int ADB_COMMAND_TIMEOUT_MILLIS = 30_000

	static final String DISABLE_PREDEX_PROPERTY_NAME = 'pl.droidsonroids.jenkins.disablepredex'
	static final String UI_TEST_MODE_PROPERTY_NAME = 'pl.droidsonroids.jenkins.ui.test.mode'

	static final String MONKEY_TASK_NAME = 'connectedMonkeyJenkinsTest'
	static final String CLEAN_UI_TEST_TEMP_DIR_TASK_NAME = 'cleanUiTestTempDir'
	static final String CONNECTED_UI_TEST_TASK_NAME = 'connectedUiTest'
	static final String CONNECTED_CHECK_TASK_NAME = 'connectedCheck'
	static final String CONNECTED_SETUP_UI_TEST_TASK_NAME = 'connectedSetupUiTest'
	static final String CLEAN_MONKEY_OUTPUT_TASK_NAME = 'cleanMonkeyOutput'

	static final String MEDIA_SCAN_COMMAND = 'am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://'
}