package pl.north93.zgame.api.bukkit.hologui;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IHoloContext
{
    Player getPlayer();

    IHoloGui getGui();

    Location getCenter();

    void setCenter(Location location);

    IIcon createIcon();

    void addIcon(IIcon icon);

    void removeIcon(IIcon icon);
}
