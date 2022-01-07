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
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
         tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
                kotlinOptions.jvmTarget = "1.8"
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
            groupId = Maven.group
            artifactId = "${Maven.artifactId}-release"
            version = "${Maven.version}.${Maven.build}"
            artifact("$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}-release.aar")

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

        register("TestRelease", MavenPublication::class) {
            groupId = Maven.group
            artifactId = "${Maven.artifactId}-test"
            version = "${Maven.version}.${Maven.build}"
            artifact("$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}-debug.aar")

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
    }
}
