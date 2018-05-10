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
    implementation(kotlin("stdlib", "1.2.41"))

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
    publishVersion = "0.1.0"
    autoPublish = true
    desc = "A Reader monad implemented in Kotlin"
    website = "https://github.com/mercari/ReaderK"
}
