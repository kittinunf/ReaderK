plugins {
    kotlin("jvm")

    id("jacoco")
}

dependencies {
    implementation(kotlin("stdlib", "1.2.41"))

    testImplementation("junit:junit:4.12")
}

jacocoTestReport {
    reports {
        xml.enabled true
        html.enabled false
    }
}
