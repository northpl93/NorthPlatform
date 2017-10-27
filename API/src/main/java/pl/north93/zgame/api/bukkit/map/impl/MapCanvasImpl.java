package pl.north93.zgame.api.bukkit.map.impl;

import static java.text.MessageFormat.format;


import java.util.Arrays;

import org.bukkit.Bukkit;

import pl.north93.zgame.api.bukkit.map.IMapCanvas;

public class MapCanvasImpl implements IMapCanvas
{
    private static final int SINGLE_MAP_SIDE = 128;
    private final int xSize, ySize;
    private final byte[] buffer;

    public MapCanvasImpl(final int xSize, final int ySize, final byte[] buffer)
    {
        this.xSize = xSize;
        this.ySize = ySize;
        this.buffer = buffer;
    }

    public MapCanvasImpl(final int xSize, final int ySize)
    {
        this(xSize, ySize, new byte[xSize * ySize]);
    }

    public static MapCanvasImpl createFromMaps(final int xMaps, final int yMaps)
    {
        return new MapCanvasImpl(xMaps * SINGLE_MAP_SIDE, yMaps * SINGLE_MAP_SIDE);
    }

    @Override
    public int getHeight()
    {
        return this.ySize;
    }

    @Override
    public int getWidth()
    {
        return this.xSize;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public void setPixel(final int x, final int y, final byte color)
    {
        if (x < 0 || y < 0 || x >= this.xSize || y >= this.ySize)
            return;

        this.buffer[y * this.ySize + x] = color;
    }

    @Override
    public byte[] getBytes()
    {
        return this.buffer;
    }

    public MapCanvasImpl getSubMapCanvas(final int xMap, final int yMap)
    {
        // definujemy nowa tablice na mape 128x129 pixeli
        final byte[] subMap = new byte[SINGLE_MAP_SIDE * SINGLE_MAP_SIDE];

        // szukamy punktu poczatkowego skad zaczynamy kopiowac
        final int startPoint = this.ySize * 128 * yMap + 64 * xMap;

        Bukkit.broadcastMessage(format("xMap={0},yMap={1}  startPoint={2}", xMap, yMap, startPoint));


        for (int lines = 0, destLoc = 0; lines < 128; lines++, destLoc += 128)
        {
            // szukamy startu aktualnej linijki.
            // kazda linijka ma 128 pixele, pie
            final int currentLineStart = startPoint + (lines * this.ySize);
            Bukkit.broadcastMessage(format("currentLineStart={0}", currentLineStart));
            System.arraycopy(this.buffer, currentLineStart, subMap, destLoc, 128);
        }

        return new MapCanvasImpl(SINGLE_MAP_SIDE, SINGLE_MAP_SIDE, subMap);
    }

    @Override
    public boolean equals(final IMapCanvas other)
    {
        return Arrays.equals(this.buffer, other.getBytes());
    }
}
