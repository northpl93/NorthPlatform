package pl.arieals.minigame.bedwars.arena.generator;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import net.minecraft.server.v1_10_R1.AxisAlignedBB;
import net.minecraft.server.v1_10_R1.EntityItem;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.cfg.BedWarsGenerator;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorItemConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorType;

public class GeneratorController
{
    private final LocalArena               arena;
    private final BedWarsGeneratorType     generatorType;
    private final Location                 location;
    private final List<ItemGeneratorEntry> entries;
    private final GeneratorHudHandler      hudHandler;

    public GeneratorController(final LocalArena arena, final BedWarsArena arenaData, final BedWarsGenerator config)
    {
        this.arena = arena;
        this.generatorType = arenaData.getGeneratorType(config.getType());
        this.location = config.getLocation().toBukkit(arena.getWorld().getCurrentWorld());
        this.entries = new ArrayList<>();
        this.setupEntries();
        this.hudHandler = new GeneratorHudHandler(this, this.entries.size() == 1); // enable hud if we have only one item
    }

    public void tick()
    {
        if (this.countItems(this.location, 1, 1, 1) >= this.generatorType.getOverload())
        {
            this.hudHandler.markOverload();
            return; // overload
        }

        final long gameTime = this.getGameTime();

        for (final ItemGeneratorEntry entry : this.entries)
        {
            entry.tick(gameTime);
        }
    }

    public LocalArena getArena() // zwraca arene do ktorej nalezy generator
    {
        return this.arena;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public BedWarsGeneratorType getGeneratorType()
    {
        return this.generatorType;
    }

    public GeneratorHudHandler getHudHandler()
    {
        return this.hudHandler;
    }

    @SuppressWarnings("unchecked")
    private int countItems(final Location loc, final double x, final double y, final double z)
    {
        final AxisAlignedBB bb = new AxisAlignedBB(loc.getX() - x, loc.getY() - y, loc.getZ() - z, loc.getX() + x, loc.getY() + y, loc.getZ() + z);
        final List<EntityItem> items = (List) ((CraftWorld) loc.getWorld()).getHandle().getEntities(null, bb, entity -> entity instanceof EntityItem);

        int count = 0;
        for (final EntityItem entity : items)
        {
            count += entity.getItemStack().count;
        }

        return count;
    }

    /*default*/ long getGameTime()
    {
        //noinspection MagicNumber
        return this.arena.getTimer().getCurrentTime(TimeUnit.SECONDS) * 20;
    }

    /*default*/ List<ItemGeneratorEntry> getEntries()
    {
        return this.entries;
    }

    private void setupEntries()
    {
        configLoop:
        for (final BedWarsGeneratorItemConfig config : this.generatorType.getItems())
        {
            for (final ItemGeneratorEntry entry : this.entries)
            {
                if (entry.tryAdd(config))
                {
                    continue configLoop; // idziemy do nastepnego configu
                }
            }
            this.entries.add(new ItemGeneratorEntry(config));
        }
    }

    /*default*/ class ItemGeneratorEntry
    {
        private final Set<BedWarsGeneratorItemConfig> items = new TreeSet<>(Comparator.comparing(BedWarsGeneratorItemConfig::getStartAt).reversed());
        private int timer;

        public ItemGeneratorEntry(final BedWarsGeneratorItemConfig initConfig)
        {
            this.items.add(initConfig);
        }

        private boolean tryAdd(final BedWarsGeneratorItemConfig config)
        {
            final BedWarsGeneratorItemConfig definition = this.items.iterator().next();
            if (definition.getMaterial() == config.getMaterial() && definition.getData() == config.getData())
            {
                this.items.add(config);
                return true;
            }
            return false;
        }

        public BedWarsGeneratorItemConfig getCurrent()
        {
            return this.items.stream()
                             .filter(config -> config.getStartAt() <= GeneratorController.this.getGameTime())
                             .findFirst().orElse(null);
        }

        private void tick(final long gameTime)
        {
            final BedWarsGeneratorItemConfig current = this.items.stream()
                                                                 .filter(config -> config.getStartAt() <= gameTime)
                                                                 .findFirst().orElse(null);

            GeneratorController.this.hudHandler.tick(current == null ? this.items.iterator().next() : current, this.timer);
            if (current == null || ++this.timer < current.getEvery())
            {
                // gdy current == null to znaczy, ze generator jeszcze nie jest wlaczony, nic nie generuje
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).append("generatorType", this.generatorType).append("location", this.location).append("entries", this.entries).toString();
    }
}
