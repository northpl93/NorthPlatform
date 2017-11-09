package pl.north93.zgame.api.bukkit.map.renderer.ranking;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class RankingEntry
{
    private final UUID   skinId;
    private final String text;

    public RankingEntry(final UUID skinId, final String text)
    {
        this.skinId = skinId;
        this.text = text;
    }

    public UUID getSkinId()
    {
        return this.skinId;
    }

    public String getText()
    {
        return this.text;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("skinId", this.skinId).append("text", this.text).toString();
    }
}