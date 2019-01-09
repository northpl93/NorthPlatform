package pl.north93.northplatform.api.bukkit.hologui.hologram.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologram;
import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologramVisibility;
import pl.north93.northplatform.api.bukkit.hologui.hologram.HologramRenderContext;
import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologramMessage;

final class HologramImpl implements IHologram
{
    private final HologramManager     hologramManager;
    private final IHologramVisibility hologramVisibility;
    private final Location            location;
    private final HologramCache       hologramCache;
    private final List<HoloLine>      lines;

    public HologramImpl(final HologramManager hologramManager, final IHologramVisibility hologramVisibility, final Location location)
    {
        this.hologramManager = hologramManager;
        this.hologramVisibility = hologramVisibility;
        this.location = location;
        this.hologramCache = new HologramCache();
        this.lines = new LinkedList<>();

        // kazdy hologram musi miec przynajmniej jedna linijke
        this.ensureLineCount(1);
    }

    @Override
    public void setMessage(final IHologramMessage text)
    {
        this.hologramCache.setMessage(text);
        for (final HoloLine line : new ArrayList<>(this.lines))
        {
            line.broadcastUpdate();
        }
    }

    @Override
    public double getLinesSpacing()
    {
        return 0.3;
    }

    @Override
    public void remove()
    {
        this.hologramManager.remove(this);
    }

    public Location getLocation()
    {
        return this.location;
    }

    private void ensureLineCount(final int count)
    {
        for (int i = this.lines.size(); i < count; i++)
        {
            this.createNextLine();
        }
    }

    private void createNextLine()
    {
        final int nextLine = this.lines.size();

        final HoloLine holoLine = new HoloLine(this, nextLine);
        this.lines.add(holoLine);
        holoLine.createArmorStand();
    }

    // ponownie inicjuje hologram po zaladowaniu chunka
    /*default*/ void tryInitHologram()
    {
        final Chunk chunk = this.location.getChunk();
        if (! chunk.isLoaded())
        {
            return;
        }

        this.lines.forEach(HoloLine::createArmorStand);
    }

    /*default*/ String getLine(final HologramRenderContext context, final int lineId)
    {
        final HologramCacheEntry cache = this.hologramCache.getEntry(context);
        this.ensureLineCount(cache.linesCount());

        return cache.getLine(lineId);
    }

    /*default*/ void setupVisibility(final ArmorStand armorStand)
    {
        this.hologramVisibility.setup(this.hologramManager.getEntityHider(), armorStand);
    }

    /*default*/ void cleanup()
    {
        final Iterator<HoloLine> iterator = this.lines.iterator();
        while (iterator.hasNext())
        {
            iterator.next().cleanup();
            iterator.remove();
        }
    }

    /*default*/ HologramManager getHologramManager()
    {
        return this.hologramManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).append("lines", this.lines).toString();
    }
}
