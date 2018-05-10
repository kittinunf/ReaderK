plugins {
    kotlin("jvm")

    jacoco
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
