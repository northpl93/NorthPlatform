package pl.north93.northplatform.api.minigame.shared.api.statistics.type;

import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.conversions.Bson;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticDbComposer;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.NumberUnit;

public class HigherNumberBetterStatistic implements IStatistic<Long, NumberUnit>
{
    private final String id;

    public HigherNumberBetterStatistic(final String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public boolean isBetter(final NumberUnit value1, final NumberUnit value2)
    {
        return value2.getValue() > value1.getValue();
    }

    @Override
    public IStatisticDbComposer<Long, NumberUnit> getDbComposer()
    {
        return HigherNumberBetterStatisticDbComposer.INSTANCE;
    }
}

class HigherNumberBetterStatisticDbComposer implements IStatisticDbComposer<Long, NumberUnit>
{
    static final HigherNumberBetterStatisticDbComposer INSTANCE = new HigherNumberBetterStatisticDbComposer();

    @Override
    public Bson bestRecordQuery()
    {
        return Sorts.descending("value");
    }

    @Override
    public NumberUnit readValue(final Document document)
    {
        final long longValue = document.get("value", Number.class).longValue();
        return new NumberUnit(longValue);
    }

    @Override
    public void insertOnlyWhenBetter(final Document document, final NumberUnit unit)
    {
        document.put("$max", new Document("value", unit.getValue()));
    }
}