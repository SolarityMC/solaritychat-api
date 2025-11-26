# SolarityChat API

An API for formatting chat messages.

## Installation

To add SolarityChat API to your project, you can use JitPack.

### Maven

Add the JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then, add the dependency:

```xml
	<dependency>
    <groupId>com.github.SolarityMC</groupId>
    <artifactId>solaritychat-api</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle

Add the JitPack repository to your `build.gradle` file:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Then, add the dependency:

```groovy
dependencies {
    implementation 'com.github.SolarityMC:solaritychat-api:LATEST' // Or a specific version
}
```
