package pl.north93.zgame.api.bukkit.map.renderer;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.map.IMapCanvas;
import pl.north93.zgame.api.bukkit.map.IMapRenderer;

public class SimpleTranslatedRenderer implements IMapRenderer
{
    @Override
    public void render(final IMapCanvas canvas, final Player player) throws Exception
    {
        final BufferedImage image = ImageIO.read(new File("C:\\Users\\Michał\\Desktop\\BANNER_LOGO.png"));
        canvas.putImage(0, 0, image);
    }
}
