package pl.north93.zgame.api.bukkit.map.impl;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.map.IBoard;
import pl.north93.zgame.api.bukkit.map.IMapRenderer;

class BoardImpl implements IBoard
{
    private final MapController mapController;
    private final int width, height;
    private final MapImpl[][] maps;
    private IMapRenderer renderer;

    public BoardImpl(final MapController mapController, final int width, final int height, final MapImpl[][] maps)
    {
        this.mapController = mapController;
        this.width = width;
        this.height = height;
        this.maps = maps;
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
        this.mapController.doRenderingFor(player, this);
    }
}
