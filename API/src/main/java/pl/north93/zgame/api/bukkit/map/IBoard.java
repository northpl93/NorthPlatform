package pl.north93.zgame.api.bukkit.map;

import java.util.Collection;

import org.bukkit.entity.Player;

public interface IBoard
{
    int getWidth();

    int getHeight();

    Collection<Player> getPlayersInRange();

    void setRenderer(IMapRenderer renderer);

    IMap getMap(int x, int y);
}
