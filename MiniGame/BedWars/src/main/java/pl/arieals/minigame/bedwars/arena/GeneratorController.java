package pl.arieals.minigame.bedwars.arena;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArenas;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.server.v1_10_R1.AxisAlignedBB;
import net.minecraft.server.v1_10_R1.EntityItem;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.cfg.BedWarsGenerator;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorItemConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorType;

public class GeneratorController
{
    private final LocalArena               arena;
    private final BedWarsGeneratorType     generatorType;
    private final Location                 location;
    private final List<ItemGeneratorEntry> entries;

    public GeneratorController(final LocalArena arena, final BedWarsArena arenaData, final BedWarsGenerator config)
    {
        this.arena = arena;
        this.generatorType = arenaData.getGeneratorType(config.getType());
        this.location = config.getLocation().toBukkit(arena.getWorld().getCurrentWorld());
        this.entries = new ArrayList<>();
        this.setupEntries();
    }

    public void tick()
    {
        if (this.countItems(this.location, 1, 1, 1) >= this.generatorType.getOverload())
        {
            return; // overload
        }

        final long gameTime = this.getGameTime();

        for (final ItemGeneratorEntry entry : this.entries)
        {
            entry.tick(gameTime);
        }
    }

    private int countItems(final Location loc, final double x, final double y, final double z)
    {
        final AxisAlignedBB bb = new AxisAlignedBB(loc.getX() - x, loc.getY() - y, loc.getZ() - z, loc.getX() + x, loc.getY() + y, loc.getZ() + z);
        return ((CraftWorld) loc.getWorld()).getHandle().getEntities(null, bb, entity -> entity instanceof EntityItem).size();
    }

    private long getGameTime()
    {
        return this.arena.getTimer().getCurrentTime(TimeUnit.SECONDS);
    }

    private void setupEntries()
    {
        for (final BedWarsGeneratorItemConfig config : this.generatorType.getItems())
        {
            for (final ItemGeneratorEntry entry : this.entries)
            {
                if (entry.tryAdd(config))
                {
                    break; // idziemy do nastepnego configu
                }
            }
            this.entries.add(new ItemGeneratorEntry(config));
        }
    }

    private class ItemGeneratorEntry
    {
        private final List<BedWarsGeneratorItemConfig> items = new ArrayList<>();
        private int timer;

        public ItemGeneratorEntry(final BedWarsGeneratorItemConfig initConfig)
        {
            this.items.add(initConfig);
        }

        private boolean tryAdd(final BedWarsGeneratorItemConfig config)
        {
            if (this.items.isEmpty())
            {
                this.items.add(config);
                return true;
            }
            final BedWarsGeneratorItemConfig definition = this.items.get(0);
            if (definition.getMaterial() == config.getMaterial() && definition.getData() == config.getData())
            {
                this.items.add(config);
                return true;
            }
            return false;
        }

        private void tick(final long gameTime)
        {
            final BedWarsGeneratorItemConfig current = this.items.stream()
                                                                 .filter(config -> config.getStartAt() <= gameTime)
                                                                 .sorted(Comparator.comparing(BedWarsGeneratorItemConfig::getStartAt))
                                                                 .findFirst().orElse(null);

            if (++this.timer < current.getEvery())
            {
                return;
            }
            this.timer = 0;

            final Location location = GeneratorController.this.location;
            final Item item = location.getWorld().dropItem(location, new ItemStack(current.getMaterial(), current.getAmount(), current.getData()));
            item.setVelocity(new Vector()); // set 0 vector
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("items", this.items).toString();
        }
    }

    public static final class GeneratorTask extends BukkitRunnable
    {
        @Override
        public void run()
        {
            for (final LocalArena arena : getArenas())
            {
                final BedWarsArena arenaData = arena.getArenaData();
                if (arenaData == null)
                {
                    continue;
                }
                arenaData.getGenerators().forEach(GeneratorController::tick);
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).append("generatorType", this.generatorType).append("location", this.location).append("entries", this.entries).toString();
    }
}
