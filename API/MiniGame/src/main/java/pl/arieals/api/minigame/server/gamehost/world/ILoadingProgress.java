package pl.arieals.api.minigame.server.gamehost.world;

import org.bukkit.World;

import pl.north93.zgame.api.bukkit.utils.ISyncCallback;

public interface ILoadingProgress extends ISyncCallback
{
    World getWorld();
}
