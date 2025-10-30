plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("jakarta.websocket:jakarta.websocket-client-api:2.1.1")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("com.google.code.gson:gson:2.12.1")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.18")
}