package pl.arieals.api.minigame.server.gamehost.world;

import java.io.File;

import pl.arieals.api.minigame.server.gamehost.utils.Cuboid;

public interface IWorldManager
{
    ILoadingProgress loadWorld(String name, File source, Cuboid gameRegion);

    ILoadingProgress regenWorld(String name);

    void unloadWorld(String name);
}
