package pl.north93.northplatform.api.global.serializer.mongodb.reader;

import java.util.Collection;

import org.bson.BsonBinary;
import org.bson.BsonRegularExpression;
import org.bson.BsonType;
import org.bson.types.ObjectId;

public interface ITypesReader
{
    boolean containsKey(String name);

    Collection<String> getKeys();

    BsonType readType(String name);

    String readString(String name);

    boolean readBoolean(String name);

    int readInt32(String name);

    double readDouble(String name);

    long readInt64(String name);

    ObjectId readObjectId(String name);

    BsonBinary readBinary(String name);

    BsonRegularExpression readRegularExpression(String name);
}
