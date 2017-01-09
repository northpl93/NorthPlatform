package pl.north93.zgame.skyblock.server.world;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.api.utils.SpiralOut;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

/**
 * Klasa zarządzająca światem z wyspami.
 * Na jednym serwerze może być wiele światów, każdy dla odpowiedniego typu wyspy.
 */
public class WorldManager
{
    private BukkitApiCore         apiCore;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer        skyBlockServer;
    private Logger                logger;
    private final IslandConfig    islandConfig;
    private final World           world;
    private final IslandList      islands;
    private final Queue<Coords2D> availableCoords;

    public WorldManager(final IslandConfig islandConfig, final World world)
    {
        this.islandConfig = islandConfig;
        this.world = world;
        this.islands = new IslandList();
        this.availableCoords = new ArrayBlockingQueue<>(100);
    }

    public void init()
    {
        this.logger.info("[SkyBlock] Downloading all islands in this world...");
        this.skyBlockServer.getIslandDao().getAllIslands(this.apiCore.getServer().get().getUuid(), this.islandConfig.getName()).stream().map(this::constructIsland).forEach(this.islands::addIsland);
        this.buildAvailableLocations();
        this.logger.info("[SkyBlock] World manager is ready.");
    }

    private void buildAvailableLocations()
    {
        this.logger.info("[SkyBlock] Building available locations...");
        final SpiralOut spiralOut = new SpiralOut();
        do
        {
            final Coords2D coords2D = spiralOut.getNextStep();
            if (this.islands.getByCoords(coords2D) == null)
            {
                this.availableCoords.add(coords2D);
            }
        }
        while (this.availableCoords.size() < 100);
    }

    // buduje obiekt wyspy na podstawie informacji o niej
    private Island constructIsland(final IslandData islandData)
    {
        final int radius = this.islandConfig.getRadius();
        final Coords2D islandLoc = islandData.getIslandLocation();
        final int diameter = radius * 2;
        final int side = (int) Math.ceil((double) diameter / 16D);

        final int centerChunkX = side * 2 * islandLoc.getX();
        final int centerChunkZ = side * 2 * islandLoc.getZ();

        return new Island(islandData, new IslandLocation(this.world, centerChunkX, centerChunkZ, radius));
    }

    public World getWorld()
    {
        return this.world;
    }

    public IslandConfig getIslandConfig()
    {
        return this.islandConfig;
    }

    public IslandList getIslands()
    {
        synchronized (this.islands)
        {
            return this.islands;
        }
    }

    public int getIslandsCount() // zwraca ilość wysp na tym świecie.
    {
        return this.islands.countIslands();
    }

    public Coords2D getFirstFreeLocation() // zwraca pierwszą wolną wyspę od środka
    {
        if (this.availableCoords.isEmpty())
        {
            this.buildAvailableLocations(); // we need more available locations!
        }
        return this.availableCoords.poll();
    }

    public void islandAdded(final IslandData islandData) // wywoływane gdy wyspa zotanie dodana
    {
        final Island island = this.constructIsland(islandData);
        this.islands.addIsland(island);
        this.apiCore.sync(island::loadSchematic); // synchronize schematic loading to main server thread
    }

    public void islandRemoved(final IslandData islandData) // wywoływane gdy wyspa zostanie usunięta
    {
        final Island island;
        island = this.islands.getByCoords(islandData.getIslandLocation());
        this.islands.removeIsland(island); // remove island from lists
        this.apiCore.sync(island::clear); // synchronize island clear to main server thread
    }

    public void islandUpdated(final IslandData islandData)
    {
        final Island island = this.islands.getByCoords(islandData.getIslandLocation());
        island.updateIslandData(islandData);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandConfig", this.islandConfig).append("world", this.world).append("islands", this.islands).append("availableCoords", this.availableCoords).toString();
    }
}
