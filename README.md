# NorthPlatform
Universal solution for Minecraft networks.

## Some history
I have started developing it in mid-2016 for my Minecraft networks (firstly skyblock, then minigames), now it's open for everyone!

## Developers
* Michał Kasprzyk (NorthPL93)
* Rafał Szewczyk (xxRafiiiisxx) - _API.GuiManager, parts of API.MiniGame, GoldHunter_
* Cestis - _Some old parts of skyblock code._

## Detailed description of modules
### [API (_/Sources/API/src_)](/Sources/API/src/)
It's a heart of whole system.
It provides API for custom extensions (called components) which allows sharing code between platforms (bukkit/bungee/standalone).
There are also core APIs that allows management of Minecraft network (players, proxies, daemons, servers).

List of features:
* Custom plugins (components) system, with multi-platform support.
* Utilizes MongoDB&Redis databases with my own serialization framework (support for bson&msgpack).
* Allows real-time communication based on redis within network.
  * Remote method execution with Java proxies.
  * Distributed locking (variation of redlock algorithm).
  * _Value_ (simple variable stored in redis with caching)
  * Events
* APIs for network management.
  * Players API with support for distributed locking (allows safe access of players data for multiple servers/proxies)
  * Real-time information about all servers, proxies and daemons running in the network.
* Basic support for groups/permissions
  * Works both on bukkit and bungee.
  * One centralised configuration (permissions.xml in network controller)
* Commands API
  * You may create commands which can be executed both on bungee and bukkit (i.e. /network)
  * Easy internationalization.
* Internationalization
  * Rich messages API with translation support.
  * Each player may choose own language.
  * Most of components are built with thinking about support for translatable messages.
* Many components built for Bukkit servers
  * Chest GUIs & hot-bar menus. (XML layout configuration)
  * Holograms (based on invisible Armor Stands)
  * GUI based on dropped items and holograms (like on Hypixel)
  * Scoreboard API
  * Simple API for packets manipulation
  
### [NetworkController (_/Sources/NetworkController/src_)](/Sources/NetworkController/src/)
It's one of the most important components.
Each network must have one instance of standalone API with this component.
Main features:
* Broadcasts configuration files in Redis. (NetworkController.ConfigServer)
* Automatically manages amount of running servers in servers groups. (NetworkController.ServersManager)

### [ServerDaemon (_/Sources/ServerDeamon/src_)](/Sources/ServerDeamon/src/)
This component starts and manages Spigot instances.

### [API.BaseFeatures (_/Sources/API/BaseFeatures/src_)](/Sources/API/BaseFeatures/src/)

### [API.Chat (_/Sources/API/Chat/src_)](/Sources/API/Chat/src/)

### [API.Economy (_/Sources/API/Economy/src_)](/Sources/API/Economy/src/)

### [API.MiniGame (_/Sources/API/MiniGame/src_)](/Sources/API/MiniGame/src/)
* Manages lifecycle of game arenas.
* Manages player's parties.
* Provides useful utils for minigame development.
* Provides integration with `NetworkController`
  (allows to automatically manage amount of servers depending on the amount of free arenas)

### [GlobalShops (_/Sources/GlobalShops/src_)](/Sources/GlobalShops/src/)
This component manages shops with persistent (between matches) items.

### [Lobby (_/Sources/Lobby/src_)](/Sources/Lobby/src/)

### [NoPremiumAuth (_/Sources/NoPremiumAuth/src_)](/Sources/NoPremiumAuth/src/)
Features required to handle both players with bought copy of game and pirates.

### [WorldProperties (_/Sources/WorldProperties/src_)](/Sources/WorldProperties/src/)

## List of minigames
### [BedWars](/Sources/MiniGame/BedWars/src)
_Developed by Michał Kasprzyk_

### [ElytraRace](/Sources/MiniGame/ElytraRace/src)
_Developed by Michał Kasprzyk_

### [GoldHunter](/Sources/MiniGame/GoldHunter/src)
_Developed by Rafał Szewczyk_