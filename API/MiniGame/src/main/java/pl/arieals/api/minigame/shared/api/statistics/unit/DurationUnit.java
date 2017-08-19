package pl.arieals.api.minigame.shared.api.statistics.unit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.IStatisticUnit;

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
        final long thisMillis = this.duration.get(ChronoUnit.MILLIS);
        final long otherMillis = durationUnit.getValue().get(ChronoUnit.MILLIS);

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
        final long millis = this.duration.get(ChronoUnit.MILLIS);
        document.put("value", millis);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("duration", this.duration).toString();
    }
}
