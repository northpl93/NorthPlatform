package pl.north93.zgame.api.bukkit.map.renderer.ranking;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.map.IMapCanvas;
import pl.north93.zgame.api.bukkit.map.IMapRenderer;

public class RankingRenderer implements IMapRenderer
{
    private final File           background;
    private final Font           font;
    private final RankingEntry[] leftRanking = new RankingEntry[10];
    private final RankingEntry[] rightRanking = new RankingEntry[10];

    public RankingRenderer(final File background)
    {
        this.background = background;
        final InputStream fontStream = this.getClass().getClassLoader().getResourceAsStream("Minecraftia-Regular.ttf");
        try
        {
            this.font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        }
        catch (final FontFormatException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setLeftPlace(final int index, final RankingEntry rankingEntry)
    {
        this.leftRanking[index] = rankingEntry;
    }

    public void setRightPlace(final int index, final RankingEntry rankingEntry)
    {
        this.rightRanking[index] = rankingEntry;
    }

    @Override
    public void render(final IMapCanvas canvas, final Player player) throws Exception
    {
        final BufferedImage image = ImageIO.read(this.background);
        final Graphics2D graphics = image.createGraphics();

        // lewa czesc rankingu
        for (int i = 0; i < 10; i++)
        {
            final RankingEntry rankingEntry = this.leftRanking[i];
            if (rankingEntry == null)
            {
                continue;
            }

            this.renderFromArray(graphics, true, i + 1, rankingEntry);
        }

        // prawa czesc rankingu
        for (int i = 0; i < 10; i++)
        {
            final RankingEntry rankingEntry = this.rightRanking[i];
            if (rankingEntry == null)
            {
                continue;
            }

            this.renderFromArray(graphics, false, i + 1, rankingEntry);
        }

        graphics.dispose();
        canvas.putImage(0, 0, image);
        //canvas.writeDebugImage(new File("C:\\Users\\Michał\\Desktop\\test.png"));
    }

    // bierze odpowiednia pozycje startowa i tworzy obiekt renderera
    private void renderFromArray(final Graphics2D graphics, final boolean isLeft, final int place, final RankingEntry rankingEntry)
    {
        final Point startPoint = new Point(isLeft ? 151 : 658, place <= 3 ? 81 : 181);
        final RankingPlaceRenderer renderer = this.createRenderer(startPoint, this.font, place);
        renderer.write(graphics, rankingEntry);
    }

    private RankingPlaceRenderer createRenderer(final Point base, final Font font, final int place)
    {
        final int targetX;
        final int targetY;

        if (place <= 3)
        {
            targetX = base.x;
            targetY = base.y + (place - 1) * 29;
        }
        else
        {
            targetX = base.x + 8;
            targetY = base.y + (place - 4) * 25;
        }

        final Color color = this.getColorFromPlace(place);

        final int skinSize = place <= 3 ? 24 : 16;

        final float fontSize = place <= 3 ? 20.8f : 17f;

        return new RankingPlaceRenderer(targetX, targetY, skinSize, 12, font.deriveFont(fontSize), color);
    }

    private Color getColorFromPlace(final int place)
    {
        switch (place)
        {
            case 1:
                return new Color(242, 235, 39);
            case 2:
                return new Color(177, 177, 177);
            case 3:
                return new Color(255, 146, 39);
            default:
                return new Color(255, 255, 255);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("font", this.font).append("leftRanking", this.leftRanking).append("rightRanking", this.rightRanking).toString();
    }
}
