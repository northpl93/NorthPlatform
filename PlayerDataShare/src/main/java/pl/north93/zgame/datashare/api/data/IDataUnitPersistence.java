package pl.north93.zgame.datashare.api.data;

import org.bson.Document;

public interface IDataUnitPersistence<T extends IDataUnit>
{
    Document toDatabase(T dataUnit);

    T fromDatabase(Document document);
}
