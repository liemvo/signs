// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath(Dependencies.gradle)
        classpath(Dependencies.kotlin)
    }
}

task("clean") {
    delete(rootProject.buildDir)
}
