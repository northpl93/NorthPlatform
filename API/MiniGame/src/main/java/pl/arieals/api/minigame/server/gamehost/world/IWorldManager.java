package pl.arieals.api.minigame.server.gamehost.world;

import java.io.File;

import pl.arieals.api.minigame.server.shared.utils.Cuboid;

public interface IWorldManager
{
    ILoadingProgress loadWorld(String name, File source, Cuboid gameRegion);

    ILoadingProgress regenWorld(String name, File source, Cuboid gameRegion);

    void unloadWorld(String name);
}
