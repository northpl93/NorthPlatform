package pl.north93.zgame.api.bukkit.map;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface IBoard
{
    World getWorld();

    int getWidth();

    int getHeight();

    Collection<Player> getPlayersInRange();

    void setRenderer(IMapRenderer renderer);

    IMap getMap(int x, int y);
}
