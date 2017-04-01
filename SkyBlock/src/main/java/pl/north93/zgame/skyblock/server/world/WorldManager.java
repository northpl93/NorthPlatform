package pl.north93.zgame.skyblock.server.world;

import java.io.File;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.shared.api.IslandData;
import pl.north93.zgame.skyblock.shared.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.shared.api.utils.Coords2D;
import pl.north93.zgame.skyblock.shared.api.utils.Coords3D;
import pl.north93.zgame.skyblock.shared.api.utils.SpiralOut;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

/**
 * Klasa zarządzająca światem z wyspami.
 * Na jednym serwerze może być wiele światów, każdy dla odpowiedniego typu wyspy.
 */
public class WorldManager
{
    private static final int WAIT_BEFORE_RECALC = 500;
    private BukkitApiCore apiCore;
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
        this.skyBlockServer.getIslandDao().getAllIslands(this.apiCore.getServer().get().getUuid(), this.islandConfig.getName())
                           .stream().map(IslandData::getIslandId).map(this::constructIsland).forEach(this.islands::addIsland);
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
    private Island constructIsland(final UUID islandId)
    {
        final Value<IslandData> islandData = this.skyBlockServer.getIslandDao().getIslandValue(islandId);

        final int radius = this.islandConfig.getRadius();
        final Coords2D islandLoc = islandData.get().getIslandLocation();
        final int diameter = radius * 2;
        final int side = (int) Math.ceil((double) diameter / 16D);

        final int centerChunkX = side * 2 * islandLoc.getX();
        final int centerChunkZ = side * 2 * islandLoc.getZ();

        return new Island(this.skyBlockServer, islandData, new IslandLocation(this.world, centerChunkX, centerChunkZ, radius));
    }

    private void loadSchematic(final Island island, final Runnable onComplete)
    {
        final Coords3D center = new Coords3D(7, this.islandConfig.getGenerateAtHeight(), 7);
        final Location location = island.getLocation().fromRelative(center);
        final File schema = new File(this.apiCore.getFile("schematics"), this.islandConfig.getSchematicName());
        SchematicUtil.pasteSchematic(location, schema, onComplete);
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
        return this.islands;
    }

    public int getIslandsCount() // zwraca ilość wysp na tym świecie.
    {
        return this.islands.countIslands();
    }

    public Coords2D getFirstFreeLocation() // zwraca pierwszą wolną wyspę od środka
    {
        synchronized (this.availableCoords)
        {
            if (this.availableCoords.isEmpty())
            {
                this.buildAvailableLocations(); // we need more available locations!
            }
            return this.availableCoords.poll();
        }
    }

    public void islandAdded(final UUID islandId) // wywoływane gdy wyspa zotanie dodana
    {
        final Island island = this.constructIsland(islandId);
        this.islands.addIsland(island);
        this.loadSchematic(island, () -> // load schematic for this island
        {
            if (this.skyBlockServer.getSkyBlockConfig().getPlaceDebugWool())
            {
                island.buildWhiteWoolMarker();
            }
            island.getPoints().recalculate(); // recalculate points after creation
        });
    }

    public void islandRemoved(final IslandData islandData) // wywoływane gdy wyspa zostanie usunięta
    {
        final Island island = this.islands.getByCoords(islandData.getIslandLocation());
        this.islands.removeIsland(island); // remove island from lists
        this.apiCore.sync(island::clear); // synchronize island clear to main server thread
        for (final Player player : island.getPlayersInIsland())
        {
            this.skyBlockServer.getServerManager().tpPlayerToSpawn(player); // we're on island host so we can safely send players to spawn
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandConfig", this.islandConfig).append("world", this.world).append("islands", this.islands).append("availableCoords", this.availableCoords).toString();
    }
}
