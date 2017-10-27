package pl.north93.zgame.api.bukkit.map.impl;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.map.IBoard;
import pl.north93.zgame.api.bukkit.map.IMapRenderer;

public class BoardImpl implements IBoard
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
    public void setRenderer(final IMapRenderer renderer)
    {
        this.renderer = renderer;
        // todo force re-render?
    }

    @Override
    public MapImpl getMap(final int x, final int y)
    {
        return this.maps[x][y];
    }

    public void renderFor(final Player player)
    {
        final MapCanvasImpl canvas = MapCanvasImpl.createFromMaps(this.width, this.height);
        this.renderer.render(canvas, player);
        this.mapController.updateFullBoard(player, this, canvas);
    }
}
