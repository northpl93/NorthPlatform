package pl.north93.northplatform.api.bukkit.map.renderer.ranking;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class RankingEntry
{
    private final UUID   skinId;
    private final String text;
    private final String result;

    public RankingEntry(final UUID skinId, final String text, final String result)
    {
        this.skinId = skinId;
        this.text = text;
        this.result = result;
    }

    public UUID getSkinId()
    {
        return this.skinId;
    }

    public String getText()
    {
        return this.text;
    }

    public String getResult()
    {
        return this.result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("skinId", this.skinId).append("text", this.text).toString();
    }
}