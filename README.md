# NorthPlatform
Universal solution for Minecraft networks.

## Developers
* Michał Kasprzyk (northpl93)
* Rafał Szewczyk (xxRafiiiisxx) - _API.GuiManager, parts of API.MiniGame, WorldProperties, GoldHunter_

## License
This project is published for reference purposes only.
You can't use any part of this code.
So, no required dependencies nor compile instructions are published.

## A detailed description of modules
### [API (_/Sources/API/src_)](/Sources/API/src/)
API is the heart of the whole system.
It manages loading of custom extensions, called components.
Component allows you to mark which piece of code belongs to which platform, it allows you to easily develop an extension which can be launched both on e.g. Bukkit and Bungee and exchanges information between these two instances.
There are also core APIs that allows management of Minecraft network (players, proxies, daemons, servers).

List of features:
* Custom plugins (components) system, with multi-platform support.
* Utilizes MongoDB&Redis databases.
  * My serializer with support for bson&messagepack.
* Real-time communication based on Redis within the network.
  * Remote method execution based on Java proxies and pub/sub.
  * Distributed locking (variation of redlock algorithm).
  * _Value_ (simple variable stored in Redis with caching)
  * Network-wide events (abstraction for pub/sub)
* APIs for network management.
  * Players API with support for distributed locking (allows multiple servers/proxies to safe access players data in Redis)
  * Real-time information about all servers, proxies and daemons running in the network.
* Basic support for groups/permissions
  * Works both on bukkit and bungee.
  * One centralised configuration (permissions.xml in network controller)
* Commands API
  * You may create commands which can be executed both on bungee and bukkit (i.e. /network)
  * Easy internationalization.
* Internationalization
  * Rich messages API with translation support.
  * Each player may choose his language.
  * Most components are built with a mind to support translatable messages.
* Many components built for Bukkit servers
  * Chest GUIs & hot-bar menus. (XML layout configuration)
  * Holograms (based on invisible Armor Stands)
  * GUI based on dropped items and holograms (like on Hypixel)
  * Scoreboard API
  * Simple API for packets manipulation

### [NetworkController (_/Sources/NetworkController/src_)](/Sources/NetworkController/src/)
It's one of the most important components.
Each network must have one instance of the standalone API with this component.
Main features:
* Broadcasts configuration files in Redis. (NetworkController.ConfigServer)
* Automatically manages the number of running servers in servers groups. (NetworkController.ServersManager)

### [ServerDaemon (_/Sources/ServerDeamon/src_)](/Sources/ServerDeamon/src/)
This component starts and manages Spigot instances.

### [API.BaseFeatures (_/Sources/API/BaseFeatures/src_)](/Sources/API/BaseFeatures/src/)
Various small features essential for all types of Minecraft servers.
* Private messages
* Support commands (/helpop)
* Server and worlds management commands (performance info etc.)
* Online time counting
* Banning and kicking

### [API.Chat (_/Sources/API/Chat/src_)](/Sources/API/Chat/src/)
Provides basic support for chat channels, formatting etc.

### [API.Economy (_/Sources/API/Economy/src_)](/Sources/API/Economy/src/)
Provides support for multiple currencies in the network.
* Supports transactional access to player's accounts.
* Supports integration with Vault.

### [API.MiniGame (_/Sources/API/MiniGame/src_)](/Sources/API/MiniGame/src/)
* Manages a lifecycle of game arenas.
* Manages player's parties.
* Provides useful utils for minigame development.
* Provides integration with `NetworkController`
  (allows to automatically manage the number of servers depending on the number of free arenas)

### [GlobalShops (_/Sources/GlobalShops/src_)](/Sources/GlobalShops/src/)
This component manages shops with persistent (between matches) items.

### [Lobby (_/Sources/Lobby/src_)](/Sources/Lobby/src/)
All features required by a lobby on a minigames server.
* User interfaces (chest GUI, hotbar, NPCs)
* Loot-chests
* Tutorials

### [NoPremiumAuth (_/Sources/NoPremiumAuth/src_)](/Sources/NoPremiumAuth/src/)
Features required to handle authentication of both players with bought a copy of game and pirates.

### [WorldProperties (_/Sources/WorldProperties/src_)](/Sources/WorldProperties/src/)
Simple settings and protection for Minecraft worlds.
[See example config.](/Sources/WorldProperties/src/main/resources/example/world-properties.xml)

## List of minigames
### [BedWars](/Sources/MiniGame/BedWars/src)
_Developed by Michał Kasprzyk_

### [ElytraRace](/Sources/MiniGame/ElytraRace/src)
_Developed by Michał Kasprzyk_

### [GoldHunter](/Sources/MiniGame/GoldHunter/src)
_Developed by Rafał Szewczyk_