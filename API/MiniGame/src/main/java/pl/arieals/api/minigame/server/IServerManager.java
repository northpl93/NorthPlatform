package pl.arieals.api.minigame.server;

import java.util.Collection;

import org.bukkit.entity.Player;

public interface IServerManager
{
    void start();

    void stop();

    void tpToHub(Collection<Player> players, String hubId);
}
