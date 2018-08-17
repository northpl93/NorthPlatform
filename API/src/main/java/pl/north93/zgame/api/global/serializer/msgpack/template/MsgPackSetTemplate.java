package pl.north93.zgame.api.global.serializer.msgpack.template;

import java.lang.reflect.Type;
import java.util.Set;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.serializer.msgpack.MsgPackDeserializationContext;
import pl.north93.zgame.api.global.serializer.msgpack.MsgPackSerializationContext;
import pl.north93.zgame.api.global.serializer.platform.CustomFieldInfo;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public class MsgPackSetTemplate implements Template<Set<Object>, MsgPackSerializationContext, MsgPackDeserializationContext>
{
    @Override
    public void serialise(final MsgPackSerializationContext context, final FieldInfo field, final Set object) throws Exception
    {
        System.out.println("SERIALIZING LIST FROM FIELD " + field);
        final MessageBufferPacker packer = context.getPacker();

        final Type genericType = this.getGenericType(context.getTemplateEngine(), field.getType());
        final Template<Object, SerializationContext, DeserializationContext> objectSerializer = context.getTemplateEngine().getTemplate(genericType);

        final FieldInfo listFieldInfo = this.createListFieldInfo(genericType);

        packer.packArrayHeader(object.size());
        for (final Object entry : object)
        {
            System.out.println("writing field of set as " + objectSerializer);
            objectSerializer.serialise(context, listFieldInfo, entry);
        }
    }

    @Override
    public Set<Object> deserialize(final MsgPackDeserializationContext context, final FieldInfo field) throws Exception
    {
        System.out.println("DESERIALIZING LIST FROM FIELD " + field);
        final MessageUnpacker unPacker = context.getUnPacker();

        final Type genericType = this.getGenericType(context.getTemplateEngine(), field.getType());
        final Template<Object, SerializationContext, DeserializationContext> objectSerializer = context.getTemplateEngine().getTemplate(genericType);

        final FieldInfo listFieldInfo = this.createListFieldInfo(genericType);
        final Set<Object> objects = this.instantiateSet(context.getTemplateEngine(), field.getType());

        final int amount = unPacker.unpackArrayHeader();
        for (int i = 0; i < amount; i++)
        {
            System.out.println("reading field of set as " + objectSerializer);
            objects.add(objectSerializer.deserialize(context, listFieldInfo));
        }

        return objects;
    }

    @SuppressWarnings("unchecked")
    private Set<Object> instantiateSet(final TemplateEngine templateEngine, final Type type)
    {
        final Class<Set<Object>> listClass = (Class<Set<Object>>) templateEngine.getRawClassFromType(type);
        return templateEngine.instantiateClass(listClass);
    }

    private FieldInfo createListFieldInfo(final Type type)
    {
        return new CustomFieldInfo(null, type);
    }

    private Type getGenericType(final TemplateEngine templateEngine, final Type type)
    {
        return templateEngine.getTypeParameters(type)[0];
    }
}
