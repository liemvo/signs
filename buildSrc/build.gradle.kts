plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

sourceSets {
    getByName("main").java.srcDir("src/main/kotlin")
}
