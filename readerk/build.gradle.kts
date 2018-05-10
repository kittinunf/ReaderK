plugins {
    kotlin("jvm")
    id("jacoco-maven-plugin")
}

dependencies {
    implementation(kotlin("stdlib", "1.2.41"))

    testImplementation("junit:junit:4.12")
}
