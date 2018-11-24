package pl.north93.northplatform.api.bungee.proxy;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.northplatform.api.global.network.server.joinaction.JoinActionsContainer;

public interface IConnectionManager
{
    void connectPlayerToServer(ProxiedPlayer player, String serverName, JoinActionsContainer actions);
}
