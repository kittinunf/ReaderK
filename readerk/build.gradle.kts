import com.novoda.gradle.release.PublishExtension

plugins {
    kotlin("jvm")

    jacoco

    id("com.novoda.bintray-release")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib", extra.get("kotlinVersion") as String))

    testImplementation("junit:junit:4.12")
}

jacoco {
    toolVersion = "0.8.1"
}

tasks {
    "jacocoTestReport"(JacocoReport::class) {
        reports {
            html.isEnabled = false
            xml.isEnabled = true
        }
    }
}

configure<PublishExtension> {
    uploadName = "ReaderK"
    groupId = "com.mercari.readerk"
    artifactId = "readerk"
    publishVersion = extra.get("publishVersion") as String
    autoPublish = true
    desc = "A Reader monad implemented in Kotlin"
    website = "https://github.com/mercari/ReaderK"
}
