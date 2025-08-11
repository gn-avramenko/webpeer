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
    implementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("jakarta.websocket:jakarta.websocket-client-api:2.2.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("com.google.code.gson:gson:2.13.1")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.18")
}