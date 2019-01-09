package pl.north93.northplatform.api.minigame.shared.api.statistics.type;

import java.time.Duration;

import org.bson.Document;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticDbComposer;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;

/**
 * Statystyka reprezentujaca czas wykonywania czynnosci.
 * Uwaza krotszy czas za lepszy.
 */
public class ShorterTimeBetterStatistic implements IStatistic<DurationUnit>
{
    private final String id;

    public ShorterTimeBetterStatistic(final String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public boolean isBetter(final DurationUnit value1, final DurationUnit value2)
    {
        // zwroci prawde jesli valu1 jest krotsze od value2
        return value1.isShorterThan(value2);
    }

    @Override
    public IStatisticDbComposer<DurationUnit> getDbComposer()
    {
        return ShorterTimeBetterStatisticDbComposer.INSTANCE;
    }
}

class ShorterTimeBetterStatisticDbComposer implements IStatisticDbComposer<DurationUnit>
{
    static final ShorterTimeBetterStatisticDbComposer INSTANCE = new ShorterTimeBetterStatisticDbComposer();

    @Override
    public Document bestRecordQuery()
    {
        return new Document("value", 1);
    }

    @Override
    public DurationUnit readValue(final Document document)
    {
        final long longValue = document.get("value", Number.class).longValue();
        return new DurationUnit(Duration.ofMillis(longValue));
    }

    @Override
    public void insertOnlyWhenBetter(final Document document, final DurationUnit unit)
    {
        document.put("$min", new Document("value", unit.getValue().toMillis()));
    }
}