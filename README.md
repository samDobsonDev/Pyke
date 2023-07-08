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

```java
public class Example {
    public static void main(String[] args) {

        Pyke pyke = new Pyke();

        pyke.registerActivePlayerEventListener(new ActivePlayerEventListener() {
            @Override
            public void onLevelUp(ActivePlayerLevelUpEvent event) {
                System.out.println(event.getAllGameData().getActivePlayer().getSummonerName() + " leveled up to level " + event.getLevel());

                if (event.getLevel() >= 16) {
                    pyke.stop(); // stops the polling and port watching
                }
            }

            @Override
            public void onAbilityLevelUp(AbilityLevelUpEvent event) {
                System.out.println(event.getAbility() + " is level " + event.getAbilityLevel());
            }

            @Override
            public void onPassive(PassiveEvent event) {
                System.out.println(event.getAllGameData().getActivePlayer().getSummonerName() + "'s passive is " + event.getPassive().getDisplayName());
            }
        });

        pyke.registerAllPlayersEventListener(new AllPlayersEventListener() {
            @Override
            public void onEyeOfHeraldUsedOrLost(EyeOfHeraldUsedOrLostEvent event) {
                System.out.println(event.getChampionName() + " used or lost the Herald!");
            }

            @Override
            public void onItemAcquired(ItemAcquiredEvent event) {
                System.out.println(event.getChampionName() + " acquired " + event.getAcquiredItem().getDisplayName());
            }

            @Override
            public void onItemSlotChange(ItemSlotChangeEvent event) {
                System.out.println(event.getChampionName() + " moved the item: " + event.getItem().getDisplayName() + " from slot " + event.getOldSlot() + " to slot " + event.getNewSlot());
            }

            @Override
            public void onItemSoldOrConsumed(ItemSoldOrConsumedEvent event) {
                System.out.println(event.getChampionName() + " sold or consumed the item: " + event.getSoldOrConsumedItem().getDisplayName());
            }

            @Override
            public void onItemTransformation(ItemTransformationEvent event) {
                System.out.println(event.getChampionName() + "'s " + event.getOldItem().getDisplayName() + " transformed into a " + event.getNewItem().getDisplayName() + "!");
            }
        });

        pyke.registerAnnouncerNotificationEventListener(new AnnouncerNotificationEventListener() {
            @Override
            public void onMinionsSpawning(MinionsSpawningEvent event) {
                System.out.println("Minions Spawning!");
            }

            @Override
            public void onFirstBlood(FirstBloodEvent event) {
                System.out.println(event.getRecipient() + " got first blood!");
            }

            @Override
            public void onGameEnd(GameEndEvent event) {
                pyke.stop();
            }
        });

        pyke.registerGameDataEventListener(new GameDataEventListener() {
            @Override
            public void onMapTerrainChange(MapTerrainChangeEvent event) {
                System.out.println("Map Terrain has changed to: " + event.getMapTerrain());
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
