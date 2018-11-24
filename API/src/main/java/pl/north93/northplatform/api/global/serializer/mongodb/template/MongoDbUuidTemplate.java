package pl.north93.northplatform.api.global.serializer.mongodb.template;

import java.util.UUID;

import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonSerializationException;

import pl.north93.northplatform.api.global.serializer.mongodb.MongoDbDeserializationContext;
import pl.north93.northplatform.api.global.serializer.mongodb.MongoDbSerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;

public class MongoDbUuidTemplate implements Template<UUID, MongoDbSerializationContext, MongoDbDeserializationContext>
{
    @Override
    public void serialise(final MongoDbSerializationContext context, final FieldInfo field, final UUID object) throws Exception
    {
        final byte[] binaryData = new byte[16];
        writeLongToArrayBigEndian(binaryData, 0, object.getMostSignificantBits());
        writeLongToArrayBigEndian(binaryData, 8, object.getLeastSignificantBits());

        reverseByteArray(binaryData, 0, 8);
        reverseByteArray(binaryData, 8, 8);

        context.getWriter().writeBinaryData(field.getName(), new BsonBinary(BsonBinarySubType.UUID_LEGACY, binaryData));
    }

    @Override
    public UUID deserialize(final MongoDbDeserializationContext context, final FieldInfo field) throws Exception
    {
        final byte[] bytes = context.readBinary(field).getData();
        if (bytes.length != 16)
        {
            throw new BsonSerializationException(String.format("Expected length to be 16, not %d.", bytes.length));
        }
        else
        {
            reverseByteArray(bytes, 0, 8);
            reverseByteArray(bytes, 8, 8);

            return new UUID(readLongFromArrayBigEndian(bytes, 0), readLongFromArrayBigEndian(bytes, 8));
        }
    }

    public static void reverseByteArray(final byte[] data, final int start, final int length)
    {
        int left = start;

        for (int right = start + length - 1; left < right; -- right)
        {
            final byte temp = data[left];
            data[left] = data[right];
            data[right] = temp;
            ++ left;
        }

    }

    private static void writeLongToArrayBigEndian(final byte[] bytes, final int offset, final long x)
    {
        bytes[offset + 7] = (byte) ((int) (255L & x));
        bytes[offset + 6] = (byte) ((int) (255L & x >> 8));
        bytes[offset + 5] = (byte) ((int) (255L & x >> 16));
        bytes[offset + 4] = (byte) ((int) (255L & x >> 24));
        bytes[offset + 3] = (byte) ((int) (255L & x >> 32));
        bytes[offset + 2] = (byte) ((int) (255L & x >> 40));
        bytes[offset + 1] = (byte) ((int) (255L & x >> 48));
        bytes[offset] = (byte) ((int) (255L & x >> 56));
    }

    private static long readLongFromArrayBigEndian(final byte[] bytes, final int offset)
    {
        long x = 0L;
        x |= 255L & (long) bytes[offset + 7];
        x |= (255L & (long) bytes[offset + 6]) << 8;
        x |= (255L & (long) bytes[offset + 5]) << 16;
        x |= (255L & (long) bytes[offset + 4]) << 24;
        x |= (255L & (long) bytes[offset + 3]) << 32;
        x |= (255L & (long) bytes[offset + 2]) << 40;
        x |= (255L & (long) bytes[offset + 1]) << 48;
        x |= (255L & (long) bytes[offset]) << 56;
        return x;
    }
}
