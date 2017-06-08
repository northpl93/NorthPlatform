package pl.north93.zgame.api.bukkit.utils.region;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.apache.commons.lang3.tuple.Pair;

public interface IRegion extends Iterable<Block>, Cloneable
{
    World getWorld();

    Location getCenter();

    List<Block> getBlocks();

    boolean contains(int x, int y, int z);

    boolean contains(Block b);

    boolean contains(Location l);

    List<Pair<Integer, Integer>> getChunksCoordinates();

    List<Chunk> getChunks();

    /**
     * Ustawia losowe koordynaty znajdujące się wewnątrz tego regionu
     * na podanym w argumencie obiekcie.
     * Podawanie obiektu jest przydatne w celu zapobiegania
     * tworzenia gigantycznej ilosci obiektów Location.
     *
     * @param ref obiekt Location w którym należy ustawić losowe koordynaty.
     * @return obiekt location podany w argumencie.
     */
    Location randomLocation(Location ref);

    default Location randomLocation()
    {
        return this.randomLocation(new Location(this.getWorld(), 0, 0, 0));
    }
}
