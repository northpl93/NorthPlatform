package pl.north93.northplatform.api.global.serializer.mongodb.reader;

/*default*/ abstract class NorthReadingContext implements ITypesReader
{
    // wchodzenie do dokumentu
    public abstract NorthReadingContext enterObject(String name);

    // wchodzenie do tablicy
    public abstract NorthReadingContext enterArray(String name);

    public abstract boolean hasMore();
}
