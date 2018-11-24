# NorthPlatform
Universal solution for Minecraft networks.

## Some history
I have started developing it in mid-2016 for my Minecraft networks (firstly skyblock, then minigames), now it's open for everyone!

## Developers
* Michał Kasprzyk (NorthPL93)
* Rafał Szewczyk (xxRafiiiisxx)
* Cestis

_Look into pom.xml files for more details._

## Detailed description of modules
### API (_/API/src_)
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
  
### Network Controller (_/Network Controller/src_)

### Server Daemon (_/Server Deamon/src_)

### API.BaseFeatures (_/API/BaseFeatures/src_)

### API.Chat (_/API/Chat/src_)

### API.Economy (_/API/Economy/src_)

### API.MiniGame (_/API/MiniGame/src_)

### GlobalShops (_/GlobalShops/src_)

### Lobby (_/Lobby/src_)

### NoPremiumAuth (_/NoPremiumAuth/src_)

### world-properties (_/WorldProperties/src_)