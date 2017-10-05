package pl.arieals.lobby.chest.loot;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.messages.TranslatableString;

/**
 * Przedstawia przedmiot ktory moze zostac dodany graczowi
 * w wyniku losowania.
 */
public interface ILoot
{
    TranslatableString getName();

    void apply(Player player);
}
