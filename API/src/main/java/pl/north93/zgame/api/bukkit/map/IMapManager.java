package pl.north93.zgame.api.bukkit.map;

import java.util.Collection;

import org.bukkit.Location;

public interface IMapManager
{
    IBoard createBoard(Location leftCorner, Location rightCorner);

    Collection<? extends IBoard> getBoards();

    void removeBoard(IBoard board);
}
