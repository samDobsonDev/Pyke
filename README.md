# Pyke
[![](https://jitpack.io/v/samDobsonDev/Pyke.svg)](https://jitpack.io/#samDobsonDev/Pyke)

Pyke is a Java library for native desktop applications designed to "hook" and process real-time game events from League of Legends matches!

## What is Pyke?

Pyke utilizes the locally hosted `/liveclientdata` API endpoint that is available when a player is in a League of Legends match. By polling this endpoint at regular intervals, Pyke detects changes in the responses and triggers any corresponding events. This allows for the development of applications that react to real-time information as it happens in-game.

## What sort of events can I receive using Pyke?

Check out the [listener classes](https://github.com/samDobsonDev/Pyke/tree/master/src/main/java/com/samdobsondev/pyke/api/listener) to get an idea of the type of events Pyke can generate.

## Requirements

Java 17 and above.

## Examples



## Installation

To include Pyke in your project, head over to [Jitpack](https://jitpack.io/#samDobsonDev/Pyke/1.0.0) and add Pyke as a dependency using your favourite dependency management tool!

### Maven:

Add Jitpack as a repository:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Add the project as a dependency:

```xml
<dependencies>
	<dependency>
	    <groupId>com.github.samDobsonDev</groupId>
	    <artifactId>Pyke</artifactId>
	    <version>1.0.0</version>
	</dependency>
</dependencies>
```

### Gradle:

Add Jitpack as a repository:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

Add the project as a dependency:

```
dependencies {
	        implementation 'com.github.samDobsonDev:Pyke:1.0.0'
}
```

## Contributing
Contributions to Pyke are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request!

## Disclaimer
Pyke is not endorsed by Riot Games and does not reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties. Riot Games, and all associated properties, are trademarks or registered trademarks of Riot Games, Inc.
