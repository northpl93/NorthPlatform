package pl.north93.zgame.api.bukkit.hologui;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IHoloGuiManager
{
    void openGui(Player player, Location location, IHoloGui holoGui);

    default void openGui(Player player, IHoloGui holoGui)
    {
        // domyslna implementacja otwiera gui dokladnie tam gdzie jest gracz
        this.openGui(player, player.getLocation(), holoGui);
    }

    void closeGui(Player player);

    @Nullable IHoloContext getPlayerContext(Player player);
}
