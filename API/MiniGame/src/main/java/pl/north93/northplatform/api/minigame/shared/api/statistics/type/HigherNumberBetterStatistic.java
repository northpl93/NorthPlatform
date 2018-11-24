package pl.north93.northplatform.api.minigame.shared.api.statistics.type;

import org.bson.Document;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticDbComposer;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.NumberUnit;

public class HigherNumberBetterStatistic implements IStatistic<NumberUnit>
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
    public IStatisticDbComposer<NumberUnit> getDbComposer()
    {
        return HigherNumberBetterStatisticDbComposer.INSTANCE;
    }
}

class HigherNumberBetterStatisticDbComposer implements IStatisticDbComposer<NumberUnit>
{
    static final HigherNumberBetterStatisticDbComposer INSTANCE = new HigherNumberBetterStatisticDbComposer();

    @Override
    public Document bestRecordQuery()
    {
        return new Document("value", -1);
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