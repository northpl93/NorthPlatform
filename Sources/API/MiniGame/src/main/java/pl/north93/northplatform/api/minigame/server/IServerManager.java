package pl.north93.northplatform.api.minigame.server;

import java.util.UUID;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;

public interface IServerManager
{
    void start();

    void stop();

    UUID getServerId();

    void tpToHub(Iterable<? extends Player> players, String hubId);

    void tpToHub(Iterable<? extends Player> players, IHubServer hubServer, String hubId);
}
