package pl.north93.northplatform.api.minigame.shared.api.statistics;

import org.bson.Document;

public interface IStatisticDbComposer<T, UNIT extends IStatisticUnit<T>>
{
    Document bestRecordQuery();

    UNIT readValue(Document document);

    void insertOnlyWhenBetter(Document document, UNIT unit);
}
