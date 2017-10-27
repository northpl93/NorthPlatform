package pl.north93.zgame.api.bukkit.map;

import org.bukkit.Location;

public interface IMapManager
{
    IBoard createBoard(Location leftCorner, Location rightCorner);
}
