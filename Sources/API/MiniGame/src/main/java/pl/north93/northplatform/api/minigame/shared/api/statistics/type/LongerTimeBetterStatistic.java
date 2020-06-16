package pl.north93.northplatform.api.minigame.shared.api.statistics.type;

import java.time.Duration;

import org.bson.Document;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticDbComposer;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;

/**
 * Statystyka reprezentujaca czas wykonywania czynnosci.
 * Uwaza dluzszy czas za lepszy.
 * Przydatne przy sumowaniu czasu spedzonym na czyms, gdzie wiecej to lepiej.
 */
public class LongerTimeBetterStatistic implements IStatistic<Duration, DurationUnit>
{
    private final String id;

    public LongerTimeBetterStatistic(final String id)
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
        return value2.isShorterThan(value1);
    }

    @Override
    public IStatisticDbComposer<Duration, DurationUnit> getDbComposer()
    {
        return LongerTimeBetterStatisticDbComposer.INSTANCE;
    }
}

class LongerTimeBetterStatisticDbComposer implements IStatisticDbComposer<Duration, DurationUnit>
{
    static final LongerTimeBetterStatisticDbComposer INSTANCE = new LongerTimeBetterStatisticDbComposer();

    @Override
    public Document bestRecordQuery()
    {
        return new Document("value", -1);
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
        document.put("$max", new Document("value", unit.getValue().toMillis()));
    }
}