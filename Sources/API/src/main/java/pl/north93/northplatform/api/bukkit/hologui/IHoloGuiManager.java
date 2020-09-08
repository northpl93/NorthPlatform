package pl.north93.northplatform.api.bukkit.hologui;

import javax.annotation.Nullable;

import org.bukkit.Location;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public interface IHoloGuiManager
{
    void openGui(INorthPlayer player, Location location, IHoloGui holoGui);

    default void openGui(INorthPlayer player, IHoloGui holoGui)
    {
        // domyslna implementacja otwiera gui dokladnie tam gdzie jest gracz
        this.openGui(player, player.getLocation(), holoGui);
    }

    void closeGui(INorthPlayer player);

    @Nullable IHoloContext getPlayerContext(INorthPlayer player);
}
