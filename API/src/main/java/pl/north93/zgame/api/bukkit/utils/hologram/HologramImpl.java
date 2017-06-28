package pl.north93.zgame.api.bukkit.utils.hologram;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

final class HologramImpl implements IHologram
{
    private final Location       location;
    private final List<HoloLine> lines;

    public HologramImpl(final Location location)
    {
        this.location = location.clone().add(0, -1, 0);
        this.lines = new LinkedList<>();
    }

    private HoloLine ensureLine(final int line, final boolean create)
    {
        for (final HoloLine holoLine : this.lines)
        {
            if (holoLine.getLineNo() == line)
            {
                return holoLine;
            }
        }

        if (! create)
        {
            return null;
        }

        final HoloLine holoLine = new HoloLine(this, line);
        this.lines.add(holoLine);
        return holoLine;
    }

    @Override
    public void clearLine(final int line)
    {
        final HoloLine holoLine = this.ensureLine(line, false);
        if (holoLine == null)
        {
            return;
        }
        holoLine.cleanup();
        this.lines.remove(holoLine);
    }

    @Override
    public void setLine(final int line, final IHologramLine text)
    {
        //noinspection ConstantConditions
        this.ensureLine(line, true).setText(text);
    }

    @Override
    public void remove()
    {
        final Iterator<HoloLine> iterator = this.lines.iterator();
        while (iterator.hasNext())
        {
            iterator.next().cleanup();
            iterator.remove();
        }
    }

    public Location getLocation()
    {
        return this.location;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).append("lines", this.lines).toString();
    }
}
