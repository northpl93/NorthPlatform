package pl.north93.northplatform.minigame.bedwars.arena.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.EntityItem;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteRandomUtils;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;
import pl.north93.northplatform.minigame.bedwars.cfg.BwConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGenerator;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGeneratorItemConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGeneratorType;

public class GeneratorController
{
    private final LocalArena               arena;
    private final BedWarsArena             arenaData;
    private final BwGeneratorType          generatorType;
    private final Location                 location;
    private final List<ItemGeneratorEntry> entries;
    private final IGeneratorHudHandler     hudHandler;

    public GeneratorController(final LocalArena arena, final BwConfig bwConfig, final BedWarsArena arenaData, final BwGenerator config)
    {
        this.arena = arena;
        this.arenaData = arenaData;
        this.generatorType = bwConfig.getGeneratorType(config.getType());
        this.location = config.getLocation().toBukkit(arena.getWorld().getCurrentWorld());
        this.entries = new ArrayList<>();
        this.setupEntries();
        this.hudHandler = this.entries.size() == 1 ? new GeneratorHudHandlerImpl(this) : new DummyGeneratorHudHandler(); // enable hud if we have only one item
    }

    public void tick()
    {
        if (this.countItems(this.location, 2, 2, 2) >= this.generatorType.getOverload())
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

    public BwGeneratorType getGeneratorType()
    {
        return this.generatorType;
    }

    public IGeneratorHudHandler getHudHandler()
    {
        return this.hudHandler;
    }

    public BedWarsArena getArenaData()
    {
        return this.arenaData;
    }

    @SuppressWarnings("unchecked")
    private int countItems(final Location loc, final double x, final double y, final double z)
    {
        final AxisAlignedBB bb = new AxisAlignedBB(loc.getX() - x, loc.getY() - y, loc.getZ() - z, loc.getX() + x, loc.getY() + y, loc.getZ() + z);
        final List<EntityItem> items = (List) ((CraftWorld) loc.getWorld()).getHandle().getEntities(null, bb, entity -> entity instanceof EntityItem);

        int count = 0;
        for (final EntityItem entity : items)
        {
            count += entity.getItemStack().getCount();
        }

        return count;
    }

    /*default*/ long getGameTime()
    {
        //noinspection MagicNumber
        return this.arena.getTimer().getCurrentTime(TimeUnit.SECONDS) * 20;
    }

    public List<ItemGeneratorEntry> getEntries() // potrzebne przy apgrejdach itp itd
    {
        return this.entries;
    }

    public void addNewEntry(final BwGeneratorItemConfig config)
    {
        for (final ItemGeneratorEntry entry : this.entries)
        {
            if (entry.tryAdd(config))
            {
                return;
            }
        }
        this.entries.add(new ItemGeneratorEntry(config));
    }

    private void setupEntries()
    {
        configLoop:
        for (final BwGeneratorItemConfig config : this.generatorType.getItems())
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

    public final class ItemGeneratorEntry
    {
        private final Set<BwGeneratorItemConfig> items = new TreeSet<>(Comparator.comparing(BwGeneratorItemConfig::getStartAt).reversed());
        private int timer;

        public ItemGeneratorEntry(final BwGeneratorItemConfig initConfig)
        {
            this.items.add(initConfig);
        }

        private boolean tryAdd(final BwGeneratorItemConfig config)
        {
            final BwGeneratorItemConfig definition = this.items.iterator().next();
            if (definition.getMaterial() == config.getMaterial() && definition.getData() == config.getData())
            {
                this.items.add(config);
                return true;
            }
            return false;
        }

        public BwGeneratorItemConfig getCurrent()
        {
            return this.items.stream()
                             .filter(config -> config.getStartAt() <= GeneratorController.this.getGameTime())
                             .findFirst().orElse(null);
        }

        private void tick(final long gameTime)
        {
            final BwGeneratorItemConfig current = this.items.stream()
                                                            .filter(config -> ! config.isTriggerable() && config.getStartAt() <= gameTime)
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

            if (GeneratorController.this.generatorType.isRandomLocation())
            {
                final double x = (DioriteRandomUtils.nextInt(6) - 2.5) / 10D;
                final double z = (DioriteRandomUtils.nextInt(6) - 2.5) / 10D;

                item.setVelocity(new Vector(x, 0.1, z));
            }
            else
            {
                item.setVelocity(new Vector()); // set 0 vector
            }
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
