package pl.arieals.api.minigame.server.gamehost.world;

import java.io.File;
import java.util.Set;

import org.bukkit.World;

import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;

public interface IWorldManager
{
    ILoadingProgress loadWorld(String name, File source, Set<XmlChunk> chunks);

    ILoadingProgress regenWorld(String name, File source, Set<XmlChunk> chunks);

    boolean clearWorld(String name);

    void trimWorld(World source, String targetName, Set<XmlChunk> chunks);
}
