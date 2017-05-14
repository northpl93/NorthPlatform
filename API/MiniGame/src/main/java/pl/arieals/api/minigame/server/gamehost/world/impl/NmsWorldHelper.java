package pl.arieals.api.minigame.server.gamehost.world.impl;

import static org.diorite.utils.reflections.DioriteReflectionUtils.getField;


import java.io.File;
import java.util.Locale;
import java.util.Map;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.EntityTracker;
import net.minecraft.server.v1_10_R1.EnumDifficulty;
import net.minecraft.server.v1_10_R1.EnumGamemode;
import net.minecraft.server.v1_10_R1.IDataManager;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.ServerNBTManager;
import net.minecraft.server.v1_10_R1.WorldData;
import net.minecraft.server.v1_10_R1.WorldServer;
import net.minecraft.server.v1_10_R1.WorldSettings;
import net.minecraft.server.v1_10_R1.WorldType;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.generator.ChunkGenerator;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class NmsWorldHelper
{
    private final CraftServer        handle  = (CraftServer) Bukkit.getServer();
    private final MinecraftServer    console = this.handle.getServer();
    private final Map<String, World> worlds; // CraftServer.worlds

    public NmsWorldHelper()
    {
        //noinspection unchecked
        this.worlds = getField(CraftServer.class, "worlds", Map.class).get(this.handle);
    }

    private File getWorldContainer()
    {
        return Bukkit.getWorldContainer();
    }

    /**
     * Tworzy nowy świat.
     * * bez wczytywania/tworzenia chunków
     * * bez konwertowania
     * * bez wywoływania eventów Bukkita
     * * spawn zostanie ustawiony jako 0/0/0 jeśli to nowy świat
     *
     * @param creator konfiguracja świata.
     * @return stworzony świat lub null jeśli się nie udało.
     */
    public World createWorld(final WorldCreator creator)
    {
        Validate.notNull(creator, "Creator may not be null");

        final String name = creator.name();
        final File folder = new File(this.getWorldContainer(), name);

        final WorldType type = WorldType.getType(creator.type().getName());
        final boolean generateStructures = creator.generateStructures();

        final World world = Bukkit.getWorld(name);
        if (world != null)
        {
            return world;
        }

        if (folder.exists() && ! folder.isDirectory())
        {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        }

        ChunkGenerator generator = creator.generator();
        if (generator == null)
        {
            generator = this.handle.getGenerator(name);
        }

        int dimension = 10 + this.console.worlds.size();
        boolean used = false;

        do
        {
            for (final WorldServer server : this.console.worlds)
            {
                used = server.dimension == dimension;
                if (used)
                {
                    ++ dimension;
                    break;
                }
            }
        } while (used);

        final IDataManager sdm = new ServerNBTManager(this.getWorldContainer(), name, true, this.console.getDataConverterManager());
        WorldData worlddata = sdm.getWorldData();

        if (worlddata == null)
        {
            final WorldSettings worldSettings = new WorldSettings(creator.seed(), EnumGamemode.getById(this.handle.getDefaultGameMode().getValue()), generateStructures, false, type);
            worldSettings.setGeneratorSettings(creator.generatorSettings());
            worlddata = new WorldData(worldSettings, name);
            worlddata.setSpawn(new BlockPosition(0, 0, 0));
            worlddata.d(true); // mark as loaded
        }

        worlddata.checkName(name);
        WorldServer internal = (WorldServer) (new WorldServer(this.console, sdm, worlddata, dimension, this.console.methodProfiler, creator.environment(), generator)).b();
        if (! this.worlds.containsKey(name.toLowerCase(Locale.ENGLISH)))
        {
            return null;
        }
        else
        {
            internal.scoreboard = this.handle.getScoreboardManager().getMainScoreboard().getHandle();
            internal.tracker = new EntityTracker(internal);
            internal.addIWorldAccess(new net.minecraft.server.v1_10_R1.WorldManager(this.console, internal));
            internal.worldData.setDifficulty(EnumDifficulty.EASY);
            internal.setSpawnFlags(true, true);
            internal.keepSpawnInMemory = false;

            this.console.worlds.add(internal);

            return internal.getWorld();
        }

    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("handle", this.handle).toString();
    }
}
