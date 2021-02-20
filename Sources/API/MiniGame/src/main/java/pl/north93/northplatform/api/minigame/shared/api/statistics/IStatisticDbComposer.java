package pl.north93.northplatform.api.minigame.shared.api.statistics;

import org.bson.Document;
import org.bson.conversions.Bson;

public interface IStatisticDbComposer<T, UNIT extends IStatisticUnit<T>>
{
    Bson bestRecordQuery();

    UNIT readValue(Document document);

    void insertOnlyWhenBetter(Document document, UNIT unit);
}
