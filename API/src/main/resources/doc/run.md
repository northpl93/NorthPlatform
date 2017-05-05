Parametry startowe API
======================
Wszystkie podawane są w następujący sposób:
```
java -Dklucz=wartosc -jar plik.jar
```

* [ALL] debug=1 - włącza tryb debugowania (wyświetla wiadomości z metody ApiCore#debug)
* [ALL] northplatform.components=sciezka - umożliwia wskazanie dodatkowego katalogu z komponentami
* [BUKKIT] northplatform.serverid=uuid - podaje UUID serwera, informacje o serwerze zostana pobrane z Redisa
* [BUKKIT] northplatform.servertype=enumServerType - podaje typ serwera; serwer wygeneruje losowe uuid i wysle dane do Redisa (niezalecane)
* [STANDALONE] northplatform.environmentFile=sciezka - umożliwia wskazanie lokalizacji pliku environment.yml (w trybie standalone definiuje on nazwę tej instancji API)