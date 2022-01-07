plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
}

android {
    compileSdk = Versions.compileSdk
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }


    buildTypes {
        getByName("release") {
            setProperty(
                "archivesBaseName",
                "$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}"
            )
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            println("##teamcity[setParameter name='target_release_version' value='${Maven.version}.${Maven.build}']")
        }
        getByName("debug") {
            setProperty(
                "archivesBaseName",
                "$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}"
            )
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(Libraries.coreKtx)
    implementation(Libraries.appcompat)
}

afterEvaluate {
    publishing {
        publications {
            register("JitpackRelease", MavenPublication::class) {
                config("release", "release")
            }
        }
    }
}

fun MavenPublication.config(artifactSuffix: String, buildSuffix: String, configVersion: String = Maven.version, build: Int = Maven.build) {
    groupId = Maven.group
    artifactId = "${Maven.artifactId}-$artifactSuffix"
    version = "${configVersion}.${build}"
    artifact("$buildDir/outputs/aar/${Maven.artifactId}-${configVersion}.${build}-$buildSuffix.aar")

    pom {
        withXml {
            // add dependencies to pom
            val dependencies = asNode().appendNode("dependencies")
            configurations.implementation.get().dependencies.forEach {
                if (it.group != null &&
                    "unspecified" != it.name &&
                    it.version != null
                ) {

                    val dependencyNode = dependencies.appendNode("dependency")
                    dependencyNode.appendNode("groupId", it.group)
                    dependencyNode.appendNode("artifactId", it.name)
                    dependencyNode.appendNode("version", it.version)
                }
            }
        }
    }
}

// publish to git package
publishing {
    repositories {
        maven {
            name = "GitHub"
            url = uri(uri("${Maven.gprBaseUrl}/${Maven.gprRepoOwner}/${Maven.gprRepoId}"))
            credentials {
                username = Maven.gprUser
                password = Maven.gprKey
            }
        }
    }
    publications {

        register("ProductionRelease", MavenPublication::class) {
            config("release", "release")
        }

        register("TestRelease", MavenPublication::class) {
            config("test", "debug")
        }
    }
}
