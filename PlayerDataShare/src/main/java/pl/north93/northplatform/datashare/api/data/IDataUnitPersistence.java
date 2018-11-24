package pl.north93.northplatform.datashare.api.data;

import org.bson.Document;

public interface IDataUnitPersistence<T extends IDataUnit>
{
    Document toDatabase(T dataUnit);

    T fromDatabase(Document document);
}
