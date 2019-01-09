package pl.north93.northplatform.api.global.serializer.mongodb;

import org.bson.BsonWriter;

import lombok.ToString;
import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;

@ToString
public class MongoDbSerializationContext extends SerializationContext
{
    private final BsonWriter writer;

    public MongoDbSerializationContext(final TemplateEngine templateEngine, final BsonWriter writer)
    {
        super(templateEngine);
        this.writer = writer;
    }

    public BsonWriter getWriter()
    {
        return this.writer;
    }

    public void writeNameIfNeeded(final FieldInfo field)
    {
        final String name = field.getName();
        if (name != null)
        {
            this.writer.writeName(name);
        }
    }

    public void writeStartArray(final FieldInfo field)
    {
        this.writeNameIfNeeded(field);
        this.writer.writeStartArray();
    }

    @Override
    public Object finalizeAndGetResult() throws Exception
    {
        return this.writer;
    }

    @Override
    public void enterObject(final FieldInfo field)
    {
        this.writeNameIfNeeded(field);
        this.writer.writeStartDocument();
    }

    @Override
    public void exitObject(final FieldInfo field)
    {
        this.writer.writeEndDocument();
    }

    @Override
    public void writeNull(final FieldInfo field) throws Exception
    {
        // nie marnujemy miejsca w dokumencie na nulle
        // aktualny deserializer poradzi sobie z tym bez problemu
    }

    @Override
    public void writeString(final FieldInfo field, final String string) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeString(string);
    }

    @Override
    public void writeBoolean(final FieldInfo field, final Boolean aBoolean) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeBoolean(aBoolean);
    }

    @Override
    public void writeByte(final FieldInfo field, final Byte aByte) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeInt32(aByte);
    }

    @Override
    public void writeShort(final FieldInfo field, final Short aShort) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeInt32(aShort);
    }

    @Override
    public void writeInteger(final FieldInfo field, final Integer integer) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeInt32(integer);
    }

    @Override
    public void writeFloat(final FieldInfo field, final Float aFloat) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeDouble(aFloat);
    }

    @Override
    public void writeDouble(final FieldInfo field, final Double aDouble) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeDouble(aDouble);
    }

    @Override
    public void writeLong(final FieldInfo field, final Long aLong) throws Exception
    {
        this.writeNameIfNeeded(field);
        this.writer.writeInt64(aLong);
    }
}
