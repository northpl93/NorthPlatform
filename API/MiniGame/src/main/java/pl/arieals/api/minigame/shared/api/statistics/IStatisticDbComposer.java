package pl.arieals.api.minigame.shared.api.statistics;

import com.mongodb.client.FindIterable;

import org.bson.Document;

public interface IStatisticDbComposer<UNIT extends IStatisticUnit>
{
    FindIterable<Document> bestRecordQuery(FindIterable<Document> in);

    UNIT readValue(Document document);

    void insertOnlyWhenBetter(Document document, UNIT unit);
}
