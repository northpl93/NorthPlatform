System komponentów umożliwia wygodne zarządzanie funkcjami pluginu w zależnośći od uruchomionej platformy (BUKKIT, BUNGEE, STANDALONE),  udostępnia także proste wstrzykiwanie zależności i system punktów rozszerzeń.

Konfiguracja komponentu
=======================
Jeden plik *.jar może zawierać wiele komponentów. Są one konfigurowane w pliku components.yml

Opis pliku components.yml
-------------------------
```yaml
include:
  - innyPlik.yml
components:
  - name: TestowyKomponent
    enabled: true
    mainClass: "pl.north93.JakasKlasa"
    description: "Opis."
    dependencies:
      - API.Database.Redis.RPC
    platforms:
      - BUKKIT
```
Klucz include umożliwia wskazanie innych plików *.yml z których mają zostać odczytane komponenty.
Klucz components to lista komponentów.

Lista parametrów w konfiguracji komponentu.
-------------------------------------------
* name - unikalna nazwa
* enabled - czy dany komponent ma być uruchamiany (Domyślnie true)
* mainClass - główna klasa, rozszerzająca Component
* autoInstantiate - czy główna klasa ma zostać utworzona automatycznie (Domyślnie true)
* description - opis
* dependencies - lista komponentów wymaganych do uruchomienia
* extensionPoints - lista punktów rozszerzeń udostępnianych przez ten komponent.
* platforms - na jakich platformach komponent ma być uruchamiany.

Paczka komponentu
=================
Za paczkę komponentu jest uznawana ta, w której znajduje się klasa główna. W jednej paczce powinien znajdować się tylko jeden komponent równocześnie np.
```
pl.north93.costam
 |-- server
   |-- CostamServerComponent (klasa główna komponentu uruchamiająca serwerową część)
 |-- bungee
   |-- CostamBungeeComponent (klasa główna komponentu uruchamiająca część bungee)
 |-- shared
   |-- inne klasy i paczki wspoldzielone
```
Dodatkową paczkę z współdzielonym kodem można dodać używając adnotacji @IncludeInScanning("") np.
```java
package pl.north93.zgame.skyblock.bungee; // paczka z kodem potrzebnym na bungee

@IncludeInScanning("pl.north93.zgame.skyblock.shared") // dodajemy paczke z kodem wspoldzielonym
public class SkyBlockBungee extends Component
{
    // reszta kodu, metody enableComponent i disableComponent
}
```
Jest to ważne ponieważ wstrzykiwanie zależności i szukanie klas implementujących extension pointy jest przeprowadzane tylko w paczkach zgłoszonych przez dany komponent.

Extension pointy
================
Punkty rozszerzeń umożliwiają stworzenie interfejsu lub klasy abstrakcyjnej, której wszystkie implementacje będą wyszukiwane i zgłaszane
do naszego handlera.

Tworzenie extension pointu
--------------------------
```java
public abstract class NorthCommand
{
    private final String       name;
    private final List<String> aliases;
    private       boolean      isAsync;
    private       String       permission;

    public NorthCommand(final String name, final String... aliases)
    {
        this.name = name;
        this.aliases = Arrays.asList(aliases);
    }
    
    // metody, toString itd
}
```
wystarczy stworzyć klasę abstrakcyjną lub interfejs i dodać go do opisu komponentu w components.yml
```yaml
components:
  - name: API.Commands
    extensionPoints:
      - "pl.north93.zgame.api.global.commands.NorthCommand"
```
i na koniec zarejestrować handler w klasie głównej komponentu (tej która rozszerza Component)
najlepiej w metodzie enableComponent
```java
public class CommandsManagerDecorator extends Component
{
    protected void enableComponent()
    {
        this.getExtensionPoint(NorthCommand.class).setHandler(this::registerCommand);
    }
    
    public void registerCommand(final NorthCommand northCommand)
    {
        // cos tam robimy z implementacja naszego extension pointu
    }
}
```

Implementacja extension pointu
------------------------------
Wystarczy rozszerzyc klase abstrakcyjna lub zaimplementowac interfejs bedacy extension pointem. Implementacja musi sie znajdowac w miejscu skanowanym przez system (opisane w sekcji Paczka komponentu).

Wstrzykiwanie zaleznosci
========================
Aby wstrzykiwanie zaleznosci dzialalo, klasa musi znajdowac sie w miejscu skanowanym przez system (opisane w sekcji Paczka komponentu).
Kod od wstrzykiwanie jest dopisywany na koniec wszystkich konstruktorów klasy która wymaga wstrzykiwania.

Co jest wstrzykiwane
--------------------
| Zmienna/Adnotacja                                               | Co jest wstrzykiwane                                                                            |
|-----------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| Logger                                                          | Wstrzykuje loggera uzywanego przez plugin API                                                   |
| ApiCore                                                         | Wstrzykuje instancje ApiCore. Można też użyć BukkitApiCore, BungeeApiCore lub StandaloneApiCore |
| @InjectComponent("nazwa") zmienna                               | Wstrzykuje klasę główną komponentu o podanej nazwie.                                            |
| @InjectResource(bundleName = "SkyBlock") ResourceBundle zmienna | Wstrzykuje ResourceBundle (plik z wiadomościami)                                                |
| @InjectNewInstance                                              | Wstrzykuje nową instancję obiektu reprezentującego zmienną                                      |
Przykład:
```java
// komenda tworząca wyspę. Jest tu zaimplementowany extension point NorthCommand, dlatego nigdzie nie trzeba tej komendy ręcznie rejestrować
public class CreateCmd extends NorthCommand
{
    private BukkitApiCore   apiCore; // wstrzykuje instance ApiCore. Ta komenda znajduje się w Bukkitowej części dlatego nie poleci ClassCastException
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager; // wstrzykuje klasę główną danego komponentu.
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle  messages; // wstrzykuje plik z wiadomościami SkyBlock_pl_PL.properties

    public CreateCmd()
    {
        super("create", "stworz");
    }
}
```