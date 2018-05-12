# ReaderK

[![jcenter](https://api.bintray.com/packages/mercari-inc/maven/ReaderK/images/download.svg)](https://bintray.com/mercari-inc/maven/ReaderK/_latestVersion) 
[![Build Status](https://circleci.com/gh/mercari/ReaderK.svg?style=svg)](https://circleci.com/gh/mercari/ReaderK)
[![codecov](https://codecov.io/gh/mercari/ReaderK/branch/master/graph/badge.svg)](https://codecov.io/gh/mercari/ReaderK)

A Reader monad implemented in Kotlin

## Ideology

`Reader<In, Out>` is to provide higher abstraction of operation requires access to the external/shared environment in order to produce the desired output.

It helps you abstract away the computation into the very last moment (until `runReader` function is called). One of the benefit for Reader monad is to make the DI (dependency injection) easy, safe and straightforward.


## Installation

### Gradle 

The latest version is [![jcenter](https://api.bintray.com/packages/mercari-inc/maven/ReaderK/images/download.svg)](https://bintray.com/mercari-inc/maven/ReaderK/_latestVersion) 

``` Groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.mercari.readerk:readerk:<latest-version>' 
}

```

### Explanation
