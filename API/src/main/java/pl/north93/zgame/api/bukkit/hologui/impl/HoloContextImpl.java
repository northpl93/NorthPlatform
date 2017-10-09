package pl.north93.zgame.api.bukkit.hologui.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.entityhider.EntityVisibility;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IHoloGui;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class HoloContextImpl implements IHoloContext
{
    @Inject
    private IEntityHider entityHider;

    private final IHoloGui      holoGui;
    private final Player        player;
    private final Set<IconImpl> icons = new HashSet<>();
    private Location            centerPoint;

    public HoloContextImpl(final IHoloGui holoGui, final Location location, final Player player)
    {
        this.holoGui = holoGui;
        this.centerPoint = location;
        this.player = player;
    }

    @Override
    public IHoloGui getGui()
    {
        return this.holoGui;
    }

    @Override
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public Location getCenter()
    {
        return this.centerPoint;
    }

    @Override
    public void setCenter(final Location location)
    {
        this.centerPoint = location;
        this.icons.forEach(IconImpl::refreshLocation);
    }

    @Override
    public IIcon createIcon()
    {
        return new IconImpl(this);
    }

    @Override
    public void addIcon(final IIcon icon)
    {
        final IconImpl impl = (IconImpl) icon;
        this.icons.add(impl);

        final Set<Entity> entity = Collections.singleton(impl.create());
        this.entityHider.setVisibility(this.player, EntityVisibility.VISIBLE, entity);
        this.entityHider.setVisibility(EntityVisibility.HIDDEN, entity);
    }

    @Override
    public void removeIcon(final IIcon icon)
    {
        final IconImpl impl = (IconImpl) icon;
        if (this.icons.remove(impl))
        {
            impl.destroy();
        }
    }

    public void handleClick(final Entity entity)
    {
        for (final IconImpl icon : new ArrayList<>(this.icons))
        {
            if (! icon.isValidClick(entity))
            {
                continue;
            }

            this.holoGui.iconClicked(this, icon);
        }
    }

    /**
     * Niszczy ten kontekst, a wiec i cale GUI.
     */
    public void destroy()
    {
        // wywoluje metode z interfejsu GUI
        this.holoGui.closeGui(this);

        // usuwa wszystkie ikony
        final Iterator<IconImpl> iterator = this.icons.iterator();
        while (iterator.hasNext())
        {
            final IconImpl next = iterator.next();
            next.destroy();
            iterator.remove();
        }
    }
}
