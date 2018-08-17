package pl.north93.zgame.api.global.serializer.msgpack;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public class MsgPackDeserializationContext extends DeserializationContext
{
    private final MessageUnpacker unpacker;

    public MsgPackDeserializationContext(final TemplateEngine templateEngine, final byte[] bytes)
    {
        super(templateEngine);
        this.unpacker = MessagePack.newDefaultUnpacker(bytes);
    }

    public MessageUnpacker getUnPacker()
    {
        return this.unpacker;
    }

    @Override
    public void enterObject(final FieldInfo field)
    {
    }

    @Override
    public void exitObject(final FieldInfo field)
    {
    }

    @Override
    public boolean trySkipNull(final FieldInfo field) throws Exception
    {
        return this.unpacker.tryUnpackNil();
    }

    @Override
    public String readString(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackString();
    }

    @Override
    public Boolean readBoolean(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackBoolean();
    }

    @Override
    public Byte readByte(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackByte();
    }

    @Override
    public Short readShort(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackShort();
    }

    @Override
    public Integer readInteger(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackInt();
    }

    @Override
    public Float readFloat(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackFloat();
    }

    @Override
    public Double readDouble(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackDouble();
    }

    @Override
    public Long readLong(final FieldInfo field) throws Exception
    {
        return this.unpacker.unpackLong();
    }
}
