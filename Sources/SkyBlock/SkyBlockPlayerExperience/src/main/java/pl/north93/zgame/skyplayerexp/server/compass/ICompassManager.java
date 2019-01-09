package pl.north93.zgame.skyplayerexp.server.compass;

import org.bukkit.entity.Player;

public interface ICompassManager
{
    void switchCompassState(Player player, boolean enabled);

    CompassConnector getCompassConnector();
}
