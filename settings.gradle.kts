rootProject.name = "web-peer"
include("demo")
include("server:core")
include("server:antd-admin")
include("web:core")
include("web:antd-admin")
pluginManagement {
    repositories {
        maven {
            url = java.net.URI("https://repo.spring.io/release")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
