# ircpokemonbot

## Description

Provides all the necessary Pokemon Info (about Pokemon, Items, Moves, Abilities and more).
It responds to commands given by the users at a specific chat room. It has a **typing prediction** feature so it will respond even if the user mistyped.
_For example, a user can type **!data cherzard** and the Bot will respond with Charizard's Base Stats, Abilities, Weaknesses/Resistance etc. It also provides a Serebii and Smogon link for the requested object._

It uses [Pokemon Showdown's](https://github.com/Zarel/Pokemon-Showdown) database. This Bot will be able to provide info about the new games' database as soon as it's available on PS.

![functionality](http://i.imgur.com/BtGHZiF.png "Functionality")

## How to run

Download [this file](https://github.com/themetalfleece/ircpokemonbot/raw/master/IRCPokemonBot.zip) and extract it. Make sure the 3 files are located in the same directory. Double click `IRCPokemonBot.jar` to run it (if it doesn't run, open it with JRE). You need to have Java Runtime Environment installed ([download here] (http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)).

**Note:** If you get an _Exception_ when you start the Bot or the fields in the configuration screen are empty (instead of having their default values), try one of the following:
1. Place both files in a directory that does not contain any special character (such as _C:\PokeBot_ or OS equivalent).
2. Run the .jar by opening a command prompt/terminal in the directory where the files are located and run the following: `java -jar IRCPokemonBot_Runnable.jar`

### Configuration for Twitch

Use a separate Twitch account for your Bot.
Press "Configure Bot" at the Main Menu.

![configuration](http://i.imgur.com/IfQBhon.png "Configucation")

| Field         | Description                                                                                                      |
| ------------- |:----------------------------------------------------------------------------------------------------------------:|
| Channel Name       | The channel to which your Bot will connect. Must begin with a # and be in **lowercase**                     |
| Bot name           | The username of the Bot account, also in **lowercase**                                                      |
| Server Hostname    | Leave as is for Twitch                                                                                      |
| Server Port        | Leave as is for Twitch                                                                                      |
| Server Password    | The oauth token of your Bot account. Get yours [here](http://www.twitchapps.com/tmi/)                       |
| Commands           | How a Twitch chat message should start for the Bot to respond to it. See more info below                    |
| Cooldown Millis     | A time period in milliseconds in which the the Bot will not respond to the same *data* commands             |
| Default Generation |  A Pokemon Generation the Bot will consider as default for the *learn* commands                             |
| Mod Only           | If ticked, the Bot will only respond to commands by Mods                                                    |
| Whispers Enabled   | If ticked, the Bot will respond to whispers. Since it's still experimental, disable if it gives you trouble |

### Commands Info

| Command  | Description                                                         | Syntax                          |
| ------------- |:-----------------------------------------------------:| ------ |
| Data     | Provides info about a **_Pokemon_**, **_Move_**, **_Item_** or **_Ability_** | !data name |
| Egg      | Provides info about the similar **_Egg Groups_** between 2 Pokemon | !egg pokeName1, pokeName2|
| Learn    | Provides info about whether a **_Pokemon_** can learn a **_Move_** in the **_Default Generation_**| !learn pokeName, moveName|
| Learn    | Provides info about whether a **_Pokemon_** can learn a **_Move_** in the **_Given Generation_**| !learn pokeName, moveName, generation|
| Commands | Provides info about the available commands and their syntax | !commands |
| Info     | Provides info about the Bot (creator, github link). | !info |

## Refresh Database

By pressing the "Refresh Database" on the Main Menu, the application will download the latest Pokemon Showdown's files and update its database. You need to use it only when PS has modified its database.
