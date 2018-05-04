package pl.arieals.api.minigame.shared.api.statistics.type;

import java.time.Instant;

import com.mongodb.client.FindIterable;

import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticDbComposer;
import pl.arieals.api.minigame.shared.api.statistics.unit.InstantUnit;

/**
 * Statystyka uznajaca pozniejszy czas wystapienia za lepszy.
 * Inaczej mowiac umozliwia ustawianie podmiotow posiadajacych te statystyke chronologicznie.
 */
public class LaterInstantBetterStatistic implements IStatistic<InstantUnit>
{
    private final String id;

    public LaterInstantBetterStatistic(final String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public boolean isBetter(final InstantUnit value1, final InstantUnit value2)
    {
        return value2.getValue().isAfter(value1.getValue());
    }

    @Override
    public IStatisticDbComposer<InstantUnit> getDbComposer()
    {
        return LaterInstantBetterStatisticDbComposer.INSTANCE;
    }
}

class LaterInstantBetterStatisticDbComposer implements IStatisticDbComposer<InstantUnit>
{
    static final LaterInstantBetterStatisticDbComposer INSTANCE = new LaterInstantBetterStatisticDbComposer();

    @Override
    public FindIterable<Document> bestRecordQuery(final FindIterable<Document> in)
    {
        return in.sort(new Document("value", -1));
    }

    @Override
    public InstantUnit readValue(final Document document)
    {
        final long longValue = document.get("value", Number.class).longValue();
        return new InstantUnit(Instant.ofEpochMilli(longValue));
    }

    @Override
    public void insertOnlyWhenBetter(final Document document, final InstantUnit unit)
    {
        document.put("$max", new Document("value", unit.getValue().toEpochMilli()));
    }
}