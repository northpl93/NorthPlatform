package pl.north93.northplatform.api.economy;

/**
 * Interfejs reprezentujacy aktualnie wykonywana transakcje.
 * Umozliwia modyfikowanie stanu konta.
 */
public interface ITransaction extends IAccountAccessor, AutoCloseable
{
    void setAmount(double newAmount);

    /**
     * Dodaje wartość do konta.
     *
     * @param amount ilość pieniędzy.
     * @return ilość pieniędzy PRZED dodaniem.
     */
    double add(double amount);

    /**
     * Usuwa wartość z konta.
     *
     * @param amount ilość pieniędzy.
     * @return ilość pieniędzy PRZED odjęcien.
     */
    double remove(double amount);

    /**
     * Sprawdza czy ta transakcja jest ciagle aktywna.
     * Jesli zostala zamknieta, ta metoda zwroci {@code false}.
     *
     * @return Czy transakcja jest jeszcze otwarta.
     */
    boolean isTransactionOpen();
}
