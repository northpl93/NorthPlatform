package pl.north93.northplatform.api.bukkit.map.impl;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.World;
import org.bukkit.entity.Player;

import lombok.ToString;
import pl.north93.northplatform.api.bukkit.map.IBoard;
import pl.north93.northplatform.api.bukkit.map.IMapRenderer;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

@ToString(of = {"world", "width", "height"})
class BoardImpl implements IBoard
{
    private final MapController mapController;
    private final World world;
    private final int width, height;
    private final MapImpl[][] maps;
    private IMapRenderer renderer;

    public BoardImpl(final MapController mapController, final World world, final int width, final int height, final MapImpl[][] maps)
    {
        this.mapController = mapController;
        this.world = world;
        this.width = width;
        this.height = height;
        this.maps = maps;
    }

    @Override
    public World getWorld()
    {
        return this.world;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    @Override
    public Collection<Player> getPlayersInRange()
    {
        final HashSet<Player> players = new HashSet<>();
        for (final MapImpl[] yMaps : this.maps)
        {
            for (final MapImpl map : yMaps)
            {
                players.addAll(map.getTrackingPlayers());
            }
        }
        return players;
    }

    @Override
    public boolean isVisibleBy(final Player player)
    {
        if (this.getWorld() != player.getWorld())
        {
            return false;
        }

        for (final MapImpl[] yMaps : this.maps)
        {
            for (final MapImpl map : yMaps)
            {
                if (map.isTrackedBy(player))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setRenderer(final IMapRenderer renderer)
    {
        this.renderer = renderer;
        this.getPlayersInRange().forEach(this::renderFor);
    }

    public @Nullable IMapRenderer getRenderer()
    {
        return this.renderer;
    }

    @Override
    public MapImpl getMap(final int x, final int y)
    {
        return this.maps[x][y];
    }

    public boolean isEntityBelongsToBoard(final int entityId)
    {
        for (final MapImpl[] yMaps : this.maps)
        {
            for (final MapImpl map : yMaps)
            {
                if (map.getFrameEntityId() == entityId)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void renderFor(final Player player)
    {
        this.mapController.doRenderingFor(INorthPlayer.wrap(player), this);
    }

    /**
     * Niszczy ta tablice i upewnia sie, ze juz nie bedzie dalo
     * sie jej uzyc.
     */
    public void cleanup()
    {
        this.renderer = null;
        for (int x = 0; x < this.width; x++)
        {
            for (int y = 0; y < this.height; y++)
            {
                this.maps[x][y].cleanup();
                this.maps[x][y] = null; // upewniamy sie ze tablica jest bezuzyteczna
            }
        }
    }
}
