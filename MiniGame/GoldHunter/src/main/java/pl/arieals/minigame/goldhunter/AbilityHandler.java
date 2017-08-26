package pl.arieals.minigame.goldhunter;

import javax.annotation.Nonnull;

import org.bukkit.Location;

public interface AbilityHandler
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
