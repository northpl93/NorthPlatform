package pl.arieals.api.minigame.shared.api.statistics.unit;

import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.IStatisticUnit;

public class InstantUnit implements IStatisticUnit<Instant>
{
    private Instant instant;

    public InstantUnit(final Instant instant)
    {
        this.instant = instant;
    }

    @Override
    public Instant getValue()
    {
        return this.instant;
    }

    @Override
    public void setValue(final Instant newValue)
    {
        this.instant = newValue;
    }

    @Override
    public void toDocument(final Document document)
    {
        document.put("value", this.instant.toEpochMilli());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("instant", this.instant).toString();
    }
}
