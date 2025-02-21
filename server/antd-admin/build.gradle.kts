plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.12.1")
    implementation(project(":server:core"))
}