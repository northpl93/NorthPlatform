package pl.arieals.api.minigame.server.gamehost.arena;

import static pl.arieals.api.minigame.shared.api.utils.InvalidGamePhaseException.checkGamePhase;


import javax.vecmath.Point3i;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.world.ILoadingProgress;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.arieals.api.minigame.server.shared.utils.Cuboid;
import pl.arieals.api.minigame.shared.api.GameMap;
import pl.arieals.api.minigame.shared.api.GamePhase;

public class ArenaWorld
{
    private final GameHostManager gameHostManager;
    private final LocalArena      arena;
    private GameMap               activeMap;
    private World                 world;
    private ILoadingProgress      progress;

    public ArenaWorld(final GameHostManager gameHostManager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.arena = arena;
    }

    public World getWorld()
    {
        return this.world;
    }

    public String getName()
    {
        return "arena_" + this.arena.getId();
    }

    public GameMap getActiveMap()
    {
        return this.activeMap;
    }

    /**
     * Sprawdza czy mapa jest załadowana.
     * @return czy mapa jest załadowana.
     */
    public boolean isReady()
    {
        return this.world != null && this.progress != null && this.progress.isComplete();
    }

    /**
     * Wczytuje wybraną mapę dla tej areny.
     * Może być wykonane tylko gdy arena znajduje się w GamePhase LOBBY.
     * @param gameMap informacje o mapie.
     */
    public void setActiveMap(final GameMap gameMap)
    {
        checkGamePhase(this.arena.getGamePhase(), GamePhase.LOBBY);
        final IWorldManager worldManager = this.gameHostManager.getWorldManager();

        final Point3i arenaRegion1 = gameMap.getArenaRegion1();
        final Point3i arenaRegion2 = gameMap.getArenaRegion2();

        final File template = new File(this.gameHostManager.getWorldTemplatesDir(), gameMap.getDirectory());
        final Cuboid gameRegion = new Cuboid(Bukkit.getWorlds().get(0), arenaRegion1.x, arenaRegion1.y, arenaRegion1.z, arenaRegion2.x, arenaRegion2.y, arenaRegion2.z);

        final ILoadingProgress progress = worldManager.regenWorld(this.getName(), template, gameRegion);

        this.activeMap = gameMap;
        this.progress = progress;
        this.world = progress.getWorld();

        progress.onComplete(() -> System.out.println("Wczytano mape!"));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.world).append("progress", this.progress).toString();
    }
}
