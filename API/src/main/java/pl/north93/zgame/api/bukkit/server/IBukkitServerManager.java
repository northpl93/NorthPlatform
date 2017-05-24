package pl.north93.zgame.api.bukkit.server;

import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;

public interface IBukkitServerManager
{
    Server getServer();

    void changeState(ServerState newState);

    boolean isShutdownScheduled();

    void scheduleShutdown();

    void cancelShutdown();
}
