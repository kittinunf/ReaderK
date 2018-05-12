buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.1.2")
        classpath(kotlin("gradle-plugin", extra.get("kotlinVersion") as String))
        classpath("com.novoda:bintray-release:0.8.0")
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
