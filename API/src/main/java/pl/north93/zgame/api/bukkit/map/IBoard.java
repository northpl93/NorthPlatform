package pl.north93.zgame.api.bukkit.map;

public interface IBoard
{
    int getWidth();

    int getHeight();

    void setRenderer(IMapRenderer renderer);

    IMap getMap(int x, int y);
}
