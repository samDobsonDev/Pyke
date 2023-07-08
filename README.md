# Pyke
[![](https://jitpack.io/v/samDobsonDev/Pyke.svg)](https://jitpack.io/#samDobsonDev/Pyke)

Pyke is a Java library for native desktop applications designed to "hook" and process real-time game events from League of Legends matches!

## What is Pyke?

Pyke utilizes the locally hosted `/liveclientdata` API endpoint that is available when a player is in a League of Legends match. By polling this endpoint at regular intervals, Pyke detects changes in the responses and triggers corresponding events based on those changes. This allows for the development of applications that react to real-time information as it happens in-game.

This library requires an active game client to be open.

## What sort of events can I receive using Pyke?

Check out the [listener classes](https://github.com/samDobsonDev/Pyke/tree/master/src/main/java/com/samdobsondev/pyke/api/listener) to get an idea of the type of events Pyke can generate.

## Requirements

Java 17 and above.

## Examples

```java
public class LaneOpponentKill {
    public static void main(String[] args) {

        Pyke pyke = new Pyke();

        pyke.registerAnnouncerNotificationEventListener(new AnnouncerNotificationEventListener() {

            // This method will trigger when any kill happens in the game
            @Override
            public void onChampionKill(ChampionKillEvent event) {

                // Grab the active player summoner name
                String activePlayerSummonerName = event.getAllGameData().getActivePlayer().getSummonerName();

                // Use the activePlayerSummonerName to retrieve the position of the active player (MID, TOP, etc..)
                String activePlayerPosition = event.getAllGameData().getAllPlayers().stream()
                        .filter(player -> player.getSummonerName().equals(activePlayerSummonerName))
                        .map(Player::getPosition)
                        .findFirst()
                        .orElse(null);

                // Find the player on the opposing team of the same position as the active player (lane opponent)
                Optional<Player> enemyMidlanerOptional = event.getAllGameData().getAllPlayers().stream()
                        .filter(player -> player.getPosition().equals(activePlayerPosition) && player.getTeam().equals("CHAOS"))
                        .findAny();
                
                while (enemyMidlanerOptional.isPresent()) {
                    Player enemyMidlaner = enemyMidlanerOptional.get();

                    // If the ChampionKillEvent killer is the active player, and the victim is the lane opponent...
                    if ((event.getKillerName().equals(event.getAllGameData().getActivePlayer().getSummonerName())) && event.getVictimName().equals(enemyMidlaner.getSummonerName())) {
                        System.out.println(activePlayerSummonerName + " killed their lane opponent!");
                    }
                }
            }
        });

        pyke.start(); // starts the port watching and subsequent polling
    }
}
```

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
