package pl.north93.zgame.api.bukkit.hologui.hologram;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

final class HologramImpl implements IHologram
{
    @Inject
    private IEntityHider              entityHider;
    @Inject
    private IBukkitExecutor           bukkitExecutor;

    private final IHologramVisibility hologramVisibility;
    private final Location            location;
    private final List<HoloLine>      lines;

    public HologramImpl(final IHologramVisibility hologramVisibility, final Location location)
    {
        this.hologramVisibility = hologramVisibility;
        this.location = location;
        this.lines = new LinkedList<>();
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
    public double getLinesSpacing()
    {
        return 0.3;
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

    public void setupVisibility(final ArmorStand armorStand)
    {
        this.hologramVisibility.setup(this.entityHider, armorStand);
    }

    public IBukkitExecutor getBukkitExecutor()
    {
        return this.bukkitExecutor;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).append("lines", this.lines).toString();
    }
}
