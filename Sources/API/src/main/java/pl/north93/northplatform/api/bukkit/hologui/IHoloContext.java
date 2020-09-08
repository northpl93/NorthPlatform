package pl.north93.northplatform.api.bukkit.hologui;

import org.bukkit.Location;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public interface IHoloContext
{
    INorthPlayer getPlayer();

    IHoloGui getGui();

    Location getCenter();

    void setCenter(Location location);

    IIcon createIcon();

    void addIcon(IIcon icon);

    void removeIcon(IIcon icon);
}
