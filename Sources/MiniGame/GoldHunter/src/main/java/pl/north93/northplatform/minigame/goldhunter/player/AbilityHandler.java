package pl.north93.northplatform.minigame.goldhunter.player;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.event.Listener;

public interface AbilityHandler extends Listener
{
    default void onReady(@Nonnull GoldHunterPlayer player)
    {
    }
    
    default boolean onUse(@Nonnull GoldHunterPlayer player, Location targetBlock)
    {
        return onUse(player);
    }
    
    default boolean onUse(@Nonnull GoldHunterPlayer player)
    {
        throw new RuntimeException("Ability handler doesn't override onUse() method");
    }
}
