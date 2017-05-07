package pl.arieals.api.minigame.server.gamehost.world;

import java.io.File;

import pl.north93.zgame.api.bukkit.utils.region.IRegion;

public interface IWorldManager
{
    ILoadingProgress loadWorld(String name, File source, IRegion gameRegion);

    ILoadingProgress regenWorld(String name, File source, IRegion gameRegion);

    boolean unloadWorld(String name);
}
