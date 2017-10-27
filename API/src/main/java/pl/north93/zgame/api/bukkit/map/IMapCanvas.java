package pl.north93.zgame.api.bukkit.map;

public interface IMapCanvas
{
    int getHeight();

    int getWidth();

    int size();

    void setPixel(int x, int y, byte color);

    byte[] getBytes();

    boolean equals(IMapCanvas other);
}
