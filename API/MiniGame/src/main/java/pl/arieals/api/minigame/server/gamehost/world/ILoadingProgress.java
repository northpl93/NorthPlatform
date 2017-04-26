package pl.arieals.api.minigame.server.gamehost.world;

import org.bukkit.World;

public interface ILoadingProgress
{
    World getWorld();

    boolean isComplete();

    void onComplete(Runnable runnable);
}
