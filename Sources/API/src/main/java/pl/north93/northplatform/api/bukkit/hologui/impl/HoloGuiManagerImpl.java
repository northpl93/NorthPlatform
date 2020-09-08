package pl.north93.northplatform.api.bukkit.hologui.impl;

import javax.annotation.Nullable;

import org.bukkit.Location;

import pl.north93.northplatform.api.bukkit.hologui.IHoloGui;
import pl.north93.northplatform.api.bukkit.hologui.IHoloGuiManager;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.Component;

public class HoloGuiManagerImpl extends Component implements IHoloGuiManager
{
    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public void openGui(final INorthPlayer player, final Location location, final IHoloGui holoGui)
    {
        // jesli jakies bylo otwarte to je zamykamy
        this.closeGui(player);

        // konfigurujemy GUI
        final HoloContextImpl holoContext = new HoloContextImpl(holoGui, location, player);
        player.setPlayerData(HoloContextImpl.class, holoContext);
        holoGui.openGui(holoContext); // wywolujemy metode renderujaca gui
    }

    @Override
    public void closeGui(final INorthPlayer player)
    {
        final HoloContextImpl playerContext = this.getPlayerContext(player);
        if (playerContext == null)
        {
            return;
        }

        playerContext.destroy();
        player.removePlayerData(HoloContextImpl.class);
    }

    @Nullable @Override
    public HoloContextImpl getPlayerContext(final INorthPlayer player)
    {
        return player.getPlayerData(HoloContextImpl.class);
    }
}
