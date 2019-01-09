package pl.north93.zgame.skyplayerexp.bungee.tablist;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;
import static pl.north93.zgame.skyplayerexp.bungee.tablist.Utils.packetJson;


import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.chat.ComponentSerializer;

class TablistSlot
{
    private static final String EMPTY_SLOT = ComponentSerializer.toString(fromLegacyText(""));
    private final int           cellId;
    private final UUID          fakePlayerId;
    private final String        sortName;
    private       String        textures;
    private       ICellProvider cellProvider;

    public TablistSlot(final int cellId, final UUID fakePlayerId, final String sortName, final String textures)
    {
        this.cellId = cellId;
        this.fakePlayerId = fakePlayerId;
        this.sortName = sortName;
        this.textures = textures;
    }

    public int getCellId()
    {
        return this.cellId;
    }

    public UUID getFakePlayerId()
    {
        return this.fakePlayerId;
    }

    public String getSortName()
    {
        return this.sortName;
    }

    public String getTextures()
    {
        return this.textures;
    }

    public void setTextures(final String textures)
    {
        this.textures = textures;
    }

    public void setCellProvider(final ICellProvider cellProvider)
    {
        this.cellProvider = cellProvider;
    }

    public String processThisCell(final TablistDrawingContext ctx)
    {
        if (this.cellProvider == null)
        {
            return EMPTY_SLOT;
        }
        return packetJson(this.cellProvider.process(ctx));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fakePlayerId", this.fakePlayerId).append("sortName", this.sortName).toString();
    }
}
