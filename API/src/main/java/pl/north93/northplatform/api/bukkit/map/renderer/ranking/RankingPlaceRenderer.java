package pl.north93.northplatform.api.bukkit.map.renderer.ranking;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class RankingPlaceRenderer
{
    private final int   x, y;
    private final int   skinSize;
    private final int   textSpacing;
    private final Font  font;
    private final Color textColor;

    public RankingPlaceRenderer(final int x, final int y, final int skinSize, final int textSpacing, final Font font, final Color textColor)
    {
        this.x = x;
        this.y = y;
        this.skinSize = skinSize;
        this.textSpacing = textSpacing;
        this.font = font;
        this.textColor = textColor;
    }

    public void write(final Graphics2D graphics, final RankingEntry rankingEntry)
    {
        final BufferedImage skin = this.extractSkin(rankingEntry.getSkinId(), this.skinSize);
        graphics.drawImage(skin, this.x, this.y, null);

        graphics.setFont(this.font);
        graphics.setColor(this.textColor);

        final int targetNickX = this.x + this.skinSize + this.textSpacing;
        graphics.drawString(rankingEntry.getText(), targetNickX, this.y + this.skinSize + 10);

        final FontMetrics fontMetrics = graphics.getFontMetrics();
        final int rankingTextWidth = fontMetrics.stringWidth(rankingEntry.getText());
        graphics.setColor(Color.WHITE);
        graphics.drawString(" - " + rankingEntry.getResult(), targetNickX + rankingTextWidth, this.y + this.skinSize + 10);

        /*final FontMetrics fontMetrics = graphics.getFontMetrics(); // wariant z punktami po prawej
        final int resultTextWidth = fontMetrics.stringWidth(rankingEntry.getResult());
        final int targetResultX = targetNickX + 425 - resultTextWidth;
        graphics.drawString(rankingEntry.getResult(), targetResultX, this.y + this.skinSize + 10);*/
    }

    private BufferedImage extractSkin(final UUID uuid, final int targetSize)
    {
        //ImageIO.read(new URL("http://skins.minecraft.net/MinecraftSkins/USERNAME.png"))
        return new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("x", this.x).append("y", this.y).append("skinSize", this.skinSize).append("textSpacing", this.textSpacing).append("font", this.font).append("textColor", this.textColor).toString();
    }
}
