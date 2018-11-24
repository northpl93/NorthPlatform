package pl.north93.northplatform.antycheat.timeline;

public interface Tick
{
    /**
     * Numer ticka od uruchomienia serwera.
     * ID teoretycznie moze sie przekrecic na minus, ale serwer musialby dzialac ponad 3 lata.
     * Dlatego mozna je uznac za unikalne i monotoniczne.
     *
     * @return Numer ticka od uruchomienia serwera.
     */
    int getTickId();

    /**
     * Czy dany tick sie zakonczyl dla antycheata.
     * <p>
     * Zakonczenie jest uznawane w miejscu wykonywania sie ITickable z NMS,
     * a wiec PO ztickowaniu entities i obsludze polaczen.
     *
     * @return Czy tick sie zakonczyl (glowna logika gry).
     */
    boolean isCompleted();
}
