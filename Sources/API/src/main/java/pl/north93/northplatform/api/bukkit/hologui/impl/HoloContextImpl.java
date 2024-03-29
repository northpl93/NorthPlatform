package pl.north93.northplatform.api.bukkit.hologui.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;

import pl.north93.northplatform.api.bukkit.hologui.IHoloContext;
import pl.north93.northplatform.api.bukkit.hologui.IHoloGui;
import pl.north93.northplatform.api.bukkit.hologui.IIcon;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

class HoloContextImpl implements IHoloContext
{
    private final IHoloGui holoGui;
    private final INorthPlayer player;
    private final Set<IconImpl> icons = new HashSet<>();
    private Location centerPoint;

    public HoloContextImpl(final IHoloGui holoGui, final Location location, final INorthPlayer player)
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
    public INorthPlayer getPlayer()
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
        impl.create();
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

    // sprawdza czy z tej lokalizacji nastapila kolizja z jakas ikona
    public void handleClick(final Location location)
    {
        for (final IconImpl icon : new ArrayList<>(this.icons))
        {
            if (icon.isLookingAt(location))
            {
                this.holoGui.iconClicked(this, icon);
                return; // obsluzylismy klikniecie, dalej nie iterujemy
            }
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
