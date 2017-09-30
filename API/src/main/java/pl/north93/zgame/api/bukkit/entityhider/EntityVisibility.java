package pl.north93.zgame.api.bukkit.entityhider;

/**
 * Reprezentuje widocznosc danego entity.
 * <p>
 * Widocznosc jest przetwarzana w kolejnosci:
 *  1. widocznosc dla gracza
 *  2. widocznosc globalna
 *
 * <table summary="Tabela prezentujaca dzialanie mechanizmu widocznosci">
 *     <tr>
 *         <td>Widocznosc gracza</td><td>Widocznosc globalna</td><td>Czy gracz widzi entity</td>
 *     </tr>
 *     <tr>
 *         <td>NEUTRAL</td><td>NEUTRAL</td><td>tak</td>
 *     </tr>
 *     <tr>
 *         <td>HIDDEN</td><td>NEUTRAL</td><td>nie</td>
 *     </tr>
 *     <tr>
 *         <td>NEUTRAL</td><td>HIDDEN</td><td>nie</td>
 *     </tr>
 *     <tr>
 *         <td>HIDDEN</td><td>VISIBLE</td><td>nie</td>
 *     </tr>
 *     <tr>
 *         <td>VISIBLE</td><td>HIDDEN</td><td>tak</td>
 *     </tr>
 * </table>
 *
 * W wypadku gdy obydwie operacje przejda jako {@link #NEUTRAL}
 * to entity bedzie widoczne.
 */
public enum EntityVisibility
{
    VISIBLE, NEUTRAL, HIDDEN;

    public EntityVisibility and(final EntityVisibility visibility)
    {
        if (visibility == NEUTRAL)
        {
            return this;
        }
        return visibility;
    }

    public boolean isVisible()
    {
        return this != HIDDEN;
    }
}
