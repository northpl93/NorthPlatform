package pl.arieals.lobby.chest.loot;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.messages.TranslatableString;

/**
 * Przedstawia przedmiot ktory moze zostac dodany graczowi
 * w wyniku losowania.
 */
public interface ILoot
{
    /**
     * Zwraca nazwe tego lootu wyswietlana graczowi.
     *
     * @return Nazwa tego lootu.
     */
    TranslatableString getName();

    /**
     * Aplikuje ten loot podanemu graczowi.
     *
     * @param player Gracz ktoremu dajemy loot.
     */
    void apply(Player player);
}
