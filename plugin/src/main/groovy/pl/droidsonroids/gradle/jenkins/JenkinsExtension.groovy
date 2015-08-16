package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.api.ApplicationVariant

public class JenkinsExtension {

    String[] testedVariants

    public void setTestedVariants(String... testedVariants) {
        this.testedVariants = testedVariants
    }

    public String[] getTestedVariants() {
        testedVariants
    }
}