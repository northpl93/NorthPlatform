package pl.north93.zgame.api.bukkit.map;

import java.awt.image.BufferedImage;
import java.io.File;

public interface IMapCanvas
{
    int getHeight();

    int getWidth();

    void setPixel(int x, int y, byte color);

    void putImage(int x, int y, BufferedImage image);

    byte getPixel(int x, int y);

    byte[] getBytes();

    void writeDebugImage(File location);

    boolean equals(IMapCanvas other);
}
