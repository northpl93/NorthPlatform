package pl.north93.northplatform.api.minigame.shared.api.statistics.unit;

import java.time.Duration;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticUnit;

public class DurationUnit implements IStatisticUnit<Duration>
{
    private Duration duration;

    public DurationUnit(final Duration duration)
    {
        this.duration = duration;
    }

    /**
     * Zwraca prawde jesli ta wartosc (this) jest krotsza od wartosci w argumencie.
     *
     * @param durationUnit Druga wartosc do porownania.
     * @return Prawda lub falsz wedlug opisu metody.
     */
    public boolean isShorterThan(final DurationUnit durationUnit)
    {
        final long thisMillis = this.duration.toMillis();
        final long otherMillis = durationUnit.getValue().toMillis();

        return thisMillis < otherMillis;
    }

    @Override
    public Duration getValue()
    {
        return this.duration;
    }

    @Override
    public void setValue(final Duration newValue)
    {
        this.duration = newValue;
    }

    @Override
    public void toDocument(final Document document)
    {
        document.put("value", this.duration.toMillis());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("duration", this.duration).toString();
    }
}
