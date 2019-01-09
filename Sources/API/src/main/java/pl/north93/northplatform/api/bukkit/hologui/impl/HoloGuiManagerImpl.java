package pl.north93.northplatform.api.bukkit.hologui.impl;

import javax.annotation.Nullable;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.Main;
import pl.north93.northplatform.api.bukkit.hologui.IHoloGui;
import pl.north93.northplatform.api.bukkit.hologui.IHoloGuiManager;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class HoloGuiManagerImpl extends Component implements IHoloGuiManager
{
    @Inject
    private BukkitApiCore apiCore;

    @Override
    protected void enableComponent()
    {
        this.apiCore.registerEvents(new HoloGuiListener(this));
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public void openGui(final Player player, final Location location, final IHoloGui holoGui)
    {
        // jesli jakies bylo otwarte to je zamykamy
        this.closeGui(player);

        // konfigurujemy GUI
        final HoloContextImpl holoContext = new HoloContextImpl(holoGui, location, player);
        this.setPlayerContext(player, holoContext);
        holoGui.openGui(holoContext); // wywolujemy metode renderujaca gui
    }

    @Override
    public void closeGui(final Player player)
    {
        final HoloContextImpl playerContext = this.getPlayerContext(player);
        if (playerContext == null)
        {
            return;
        }

        playerContext.destroy();
        this.setPlayerContext(player, null);
    }

    @Nullable @Override
    public HoloContextImpl getPlayerContext(final Player player)
    {
        final List<MetadataValue> metadata = player.getMetadata("holoGui/context");
        if (metadata.isEmpty())
        {
            return null;
        }
        return (HoloContextImpl) metadata.get(0).value();
    }

    public void setPlayerContext(final Player player, final HoloContextImpl holoContext)
    {
        final Main plugin = this.apiCore.getPluginMain();

        if (holoContext == null)
        {
            player.removeMetadata("holoGui/context", plugin);
        }
        else
        {
            player.setMetadata("holoGui/context", new FixedMetadataValue(plugin, holoContext));
        }
    }
}
