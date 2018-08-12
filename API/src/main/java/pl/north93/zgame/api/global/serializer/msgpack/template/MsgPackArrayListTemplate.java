package pl.north93.zgame.api.global.serializer.msgpack.template;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.serializer.msgpack.MsgPackDeserializationContext;
import pl.north93.zgame.api.global.serializer.msgpack.MsgPackSerializationContext;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;

public class MsgPackArrayListTemplate implements Template<ArrayList, MsgPackSerializationContext, MsgPackDeserializationContext>
{
    static class ArrayListFieldInfo implements FieldInfo
    {
        @Override
        public String getName()
        {
            return "entry";
        }

        @Override
        public Type getType()
        {
            return Object.class;
        }
    }

    @Override
    public void serialise(final MsgPackSerializationContext context, final FieldInfo field, final ArrayList object) throws Exception
    {
        final MessageBufferPacker packer = context.getPacker();
        final Template<Object, SerializationContext, DeserializationContext> objectSerializer = context.getTemplateEngine().getTemplate(Object.class);

        packer.packArrayHeader(object.size());
        for (final Object entry : object)
        {
            objectSerializer.serialise(context, new ArrayListFieldInfo(), entry);
        }
    }

    @Override
    public ArrayList deserialize(final MsgPackDeserializationContext context, final FieldInfo field) throws Exception
    {
        final MessageUnpacker unPacker = context.getUnPacker();
        final Template<Object, SerializationContext, DeserializationContext> objectSerializer = context.getTemplateEngine().getTemplate(Object.class);

        final int amount = unPacker.unpackArrayHeader();

        final ArrayList<Object> objects = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++)
        {
            objects.add(objectSerializer.deserialize(context, new ArrayListFieldInfo()));
        }

        return objects;
    }
}
