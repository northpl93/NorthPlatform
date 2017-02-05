package pl.north93.zgame.skyplayerexp.bungee.tablistarieals;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.bungee.SkyBlockBungee;
import pl.north93.zgame.skyplayerexp.bungee.tablist.ICellProvider;
import pl.north93.zgame.skyplayerexp.bungee.tablist.TablistDrawingContext;

public class IslandSizeProvider implements ICellProvider
{
    @InjectComponent("SkyBlock.Proxy")
    private SkyBlockBungee skyblock;

    /*@Override
    public String process(final TablistDrawingContext ctx)
    {
        if (! ctx.hasIsland())
        {
            return "&6rozmiar: &r--";
        }
        final int size = this.skyblock.getConfig().getIslandType(ctx.getIslandData().getIslandType()).getRadius() * 2;
        return "&6rozmiar: &r" + size + "x" + size;
    }*/

    public String process(final TablistDrawingContext ctx)
    {
        if (! ctx.hasIsland())
        {
            return "&6ranking: &r--";
        }
        final long pos = this.skyblock.getRanking().getPosition(ctx.getIslandData().getIslandId());
        return "&6ranking: &r" + (pos + 1);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
