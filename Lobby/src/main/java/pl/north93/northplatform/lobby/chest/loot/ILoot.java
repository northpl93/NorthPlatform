package pl.north93.northplatform.lobby.chest.loot;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.hologui.IIcon;
import pl.north93.northplatform.api.global.messages.TranslatableString;

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
     * Konfiguruje ikone tego lootu.
     *
     * @param icon Ikona do skonfigurowania.
     */
    void setupIcon(IIcon icon);

    /**
     * Aplikuje ten loot podanemu graczowi.
     *
     * @param player Gracz ktoremu dajemy loot.
     */
    void apply(Player player);
}
