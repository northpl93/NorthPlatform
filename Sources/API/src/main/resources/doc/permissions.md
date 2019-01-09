Standard definiowania uprawnień
================================
1. Separatorem poszczególnych bloków uprawnienia jest kropka.
2. Całe uprawnienie jest zapisane małymi literami.
3. Każde uprawnienie zaczyna się od nazwy określającej komponent lub grupę komponentów.
    * Skrócona nazwa modułu mavena zawierającego komponent definujący uprawnienie.
4. Komendy po prefixie dodatkowo zawierają `cmd` np `basefeatures.cmd.example`
    * Jako nazwy używamy wartości podawanej w pierwszym argumencie konstruktora `NorthCommand` (NIE aliasy!)
5. Uprawnienie `dev` jest zarezerwowane dla komend i funkcji testowych, które nigdy nie powinny byc uzywane na produkcji.

| Komponenty                             | Prefix          |
|----------------------------------------|-----------------|
| AntyCheat.Core                         | antycheat       |
| API.BaseFeatures (wszystkie platformy) | basefeatures    |
| API.Chat (wszystkie platformy)         | chat            |
| API.Economy (wszystkie platformy)      | economy         |
| API.MiniGame (wszystkie platformy)     | minigameapi     |
| API (wszystkie platformy)              | api             |
| Lobby                                  | lobby           |
| NoPremiumAuth (wszystkie platformy)    | nopremiumauth   |
| world-properties                       | worldproperties |


Stara zawartość tego pliku (do usunięcia)
=========================================
| Nazwa uprawnienia         	| Opis                                         	|
|---------------------------	|----------------------------------------------	|
| join.admin                	| JoiningPolicy.ONLY_ADMIN                     	|
| join.vip                  	| JoiningPolicy.ONLY_VIP                       	|
| join.bypass               	| Wchodzenia na serwer gdy jest pełny          	|
| chat.colorize             	| Automatyczne kolory na czacie                	|
| sign.colorize                 | Kolorowanie tabliczek                         |
| api.command.chat          	| Zarządzanie czatem                           	|
| chat.bypass               	| Pisanie na czacie gdy jest wyłączony         	|
| api.command.network       	| Zarządzanie siecią                           	|
| api.command.playerinfo    	| Informacje o graczu                          	|
| api.command.joiningpolicy 	| Zarządzanie wchodzeniem graczy na serwer     	|
| dev                       	| Dla programisty                              	|
| skyblock.admin            	| Komendy administratora                       	|
| skyblock.island.TypeName  	| Uprawnienia do tworzenia wyspy typu TypeName 	|