package pl.arieals.api.minigame.shared.api.statistics;

import org.bson.Document;

public interface IStatisticDbComposer<UNIT extends IStatisticUnit>
{
    Document bestRecordQuery();

    UNIT readValue(Document document);

    void insertOnlyWhenBetter(Document document, UNIT unit);
}
