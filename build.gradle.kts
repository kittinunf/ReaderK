buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", extra.get("kotlinVersion") as String))
        classpath("com.novoda:bintray-release:${extra.get("bintrayReleaseVersion")}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
