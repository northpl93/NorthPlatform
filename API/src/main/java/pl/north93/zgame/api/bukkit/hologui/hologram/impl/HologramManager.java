package pl.north93.zgame.api.bukkit.hologui.hologram.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologramVisibility;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/*default*/ class HologramManager
{
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private IEntityHider    entityHider;
    @Inject
    private IBukkitExecutor bukkitExecutor;

    private final List<HologramImpl> holograms;

    @Bean
    private HologramManager()
    {
        this.holograms = new ArrayList<>();
    }

    /**
     * Tworzy nowy hologram.
     *
     * @param hologramVisibility Polityka widoczności danego hologramu.
     * @param location Lokalizacja hologramu.
     * @return Instancja reprezentująca utworzony hologram.
     */
    public HologramImpl createHologram(final IHologramVisibility hologramVisibility, final Location location)
    {
        final HologramImpl hologram = new HologramImpl(this, hologramVisibility, location);
        this.holograms.add(hologram);

        return hologram;
    }

    /**
     * Zwraca wszystkie hologramy znajdujące się w danym chunku.
     *
     * @param chunk Chunk z którego pobieramy hologramy.
     * @return Kolekcja hologramów w danym chunku.
     */
    public Collection<HologramImpl> getHologramsInChunk(final Chunk chunk)
    {
        final Predicate<HologramImpl> predicate = hologram -> hologram.getLocation().getChunk() == chunk;
        return this.holograms.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Usuwa dany hologram.
     *
     * @param hologram Hologram do usunięcia.
     */
    public void remove(final HologramImpl hologram)
    {
        hologram.cleanup();
        this.holograms.remove(hologram);
    }

    /**
     * Usuwa wszystkie hologramy znajdujące się w danym świecie.
     *
     * @param world Swiat do wyczyszczenia z hologramów.
     */
    public void removeFromWorld(final World world)
    {
        this.holograms.removeIf(hologram ->
        {
            final World hologramWorld = hologram.getLocation().getWorld();
            if (hologramWorld == world)
            {
                hologram.cleanup();
                return true;
            }

            return false;
        });
    }

    public BukkitApiCore getApiCore()
    {
        return this.apiCore;
    }

    public IEntityHider getEntityHider()
    {
        return this.entityHider;
    }

    public IBukkitExecutor getBukkitExecutor()
    {
        return this.bukkitExecutor;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("holograms", this.holograms).toString();
    }
}
