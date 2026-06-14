// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    // Project 2: Firebase. Declared here, applied conditionally in app/build.gradle.kts
    alias(libs.plugins.google.services) apply false
}
