package pl.north93.northplatform.api.bukkit.hologui.hologram.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;

import lombok.ToString;
import pl.north93.northplatform.api.bukkit.hologui.hologram.HologramRenderContext;
import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologramMessage;

@ToString
/*default*/ class HologramCache
{
    private final Map<Player, HologramCacheEntry> cache = new WeakHashMap<>();
    private IHologramMessage message;

    public void setMessage(final IHologramMessage message)
    {
        this.message = message;
        this.cache.clear();
    }

    public String getLine(final HologramRenderContext context, final int lineNumber)
    {
        return this.getEntry(context).getLine(lineNumber);
    }

    /*default*/ HologramCacheEntry getEntry(final HologramRenderContext context)
    {
        if (this.message == null)
        {
            return HologramCacheEntry.EMPTY;
        }

        return this.cache.computeIfAbsent(context.getPlayer(), player ->
        {
            return new HologramCacheEntry(this.message.render(context));
        });
    }
}

@ToString(of = "lines")
/*default*/ class HologramCacheEntry
{
    /*default*/ static final HologramCacheEntry EMPTY = new HologramCacheEntry(Collections.emptyList());
    private final List<String> lines;

    public HologramCacheEntry(final List<String> lines)
    {
        this.lines = lines;
    }

    public int linesCount()
    {
        return this.lines.size();
    }

    public String getLine(final int lineNumber)
    {
        final int linesCount = this.lines.size();
        if (linesCount > lineNumber)
        {
            final int lineIndex = linesCount - lineNumber - 1;
            return this.lines.get(lineIndex);
        }

        return StringUtils.EMPTY;
    }

    public List<String> getLines()
    {
        return this.lines;
    }
}