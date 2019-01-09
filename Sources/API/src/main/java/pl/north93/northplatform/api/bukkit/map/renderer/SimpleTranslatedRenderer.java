package pl.north93.northplatform.api.bukkit.map.renderer;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.map.IMapCanvas;
import pl.north93.northplatform.api.bukkit.map.IMapRenderer;
import pl.north93.northplatform.api.bukkit.map.loader.xml.XmlTranslatableImage;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public class SimpleTranslatedRenderer implements IMapRenderer
{
    private final Map<Locale, CachedBoardData> filesMap = new HashMap<>(2);

    public SimpleTranslatedRenderer()
    {
    }

    public SimpleTranslatedRenderer(final XmlTranslatableImage translatableImage)
    {
        for (final XmlTranslatableImage.ImageEntry entry : translatableImage.getImages())
        {
            final Locale locale = Locale.forLanguageTag(entry.getLanguage());
            final File file = new File(entry.getFile());

            this.setFileForLanguage(locale, file);
        }
    }

    public void setFileForLanguage(final Locale locale, final File file)
    {
        this.filesMap.put(locale, new CachedBoardData(file));
    }

    @Override
    public void render(final IMapCanvas canvas, final INorthPlayer player) throws Exception
    {
        final CachedBoardData boardData = this.filesMap.get(player.getMyLocale());

        boardData.renderToCanvas(canvas);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("filesMap", this.filesMap).toString();
    }
}

class CachedBoardData
{
    private final File file;
    private IMapCanvas canvas;

    public CachedBoardData(final File file)
    {
        this.file = file;
    }

    public void renderToCanvas(final IMapCanvas mapCanvas) throws Exception
    {
        if (this.canvas == null)
        {
            // metoda renderuje w wlasciwym canvasie i tworzy z tego cache
            this.generateCache(mapCanvas);
        }
        else
        {
            // jak mamy juz wygenerowane cache to tylko wklejamy zcachowany canvas do wlasciwego
            mapCanvas.putCanvas(0, 0, this.canvas);
        }
    }

    private void generateCache(final IMapCanvas mapCanvas) throws Exception
    {
        final BufferedImage image = ImageIO.read(this.file);
        mapCanvas.putImage(0, 0, image);

        // cachujemy skopiowana instancje
        this.canvas = mapCanvas.clone();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("file", this.file).toString();
    }
}