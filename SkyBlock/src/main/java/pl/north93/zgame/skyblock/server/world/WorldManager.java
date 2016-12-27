package pl.north93.zgame.skyblock.server.world;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import org.bukkit.World;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
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
        this.skyBlockServer.getIslandDao().getAllIsland(this.apiCore.getServer().get().getUuid(), this.islandConfig.getName()).forEach(this.islands::addIsland);
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

    public World getWorld()
    {
        return this.world;
    }

    public Coords2D getFirstFreeLocation()
    {
        if (this.availableCoords.isEmpty())
        {
            this.buildAvailableLocations(); // we need more available locations!
        }
        return this.availableCoords.poll();
    }
}
