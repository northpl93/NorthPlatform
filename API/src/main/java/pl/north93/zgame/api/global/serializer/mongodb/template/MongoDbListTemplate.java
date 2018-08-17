package pl.north93.zgame.api.global.serializer.mongodb.template;

import java.lang.reflect.Type;
import java.util.List;

import pl.north93.zgame.api.global.serializer.mongodb.MongoDbDeserializationContext;
import pl.north93.zgame.api.global.serializer.mongodb.MongoDbSerializationContext;
import pl.north93.zgame.api.global.serializer.platform.CustomFieldInfo;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public class MongoDbListTemplate implements Template<List<Object>, MongoDbSerializationContext, MongoDbDeserializationContext>
{
    @Override
    public void serialise(final MongoDbSerializationContext context, final FieldInfo field, final List object) throws Exception
    {
        System.out.println("SERIALIZING LIST FROM FIELD " + field);

        final Type genericType = this.getGenericType(context.getTemplateEngine(), field.getType());
        final Template<Object, SerializationContext, DeserializationContext> objectSerializer = context.getTemplateEngine().getTemplate(genericType);

        final FieldInfo listFieldInfo = this.createListFieldInfo(genericType);

        context.writeStartArray(field);
        for (final Object entry : object)
        {
            System.out.println("writing field of arraylist as " + objectSerializer);
            objectSerializer.serialise(context, listFieldInfo, entry);
        }
        context.getWriter().writeEndArray();
    }

    @Override
    public List<Object> deserialize(final MongoDbDeserializationContext context, final FieldInfo field) throws Exception
    {
        System.out.println("DESERIALIZING LIST FROM FIELD " + field);
        final Type genericType = this.getGenericType(context.getTemplateEngine(), field.getType());
        final Template<Object, SerializationContext, DeserializationContext> objectSerializer = context.getTemplateEngine().getTemplate(genericType);

        final FieldInfo listFieldInfo = this.createListFieldInfo(genericType);

        context.readStartArray(field);

        final List<Object> objects = this.instantiateList(context.getTemplateEngine(), field.getType());
        while (context.hasMore())
        {
            System.out.println("reading field of list as " + objectSerializer);
            objects.add(objectSerializer.deserialize(context, listFieldInfo));
        }

        context.readEndArray(field);

        return objects;
    }

    private FieldInfo createListFieldInfo(final Type type)
    {
        return new CustomFieldInfo(null, type);
    }

    private Type getGenericType(final TemplateEngine templateEngine, final Type type)
    {
        return templateEngine.getTypeParameters(type)[0];
    }

    @SuppressWarnings("unchecked")
    private List<Object> instantiateList(final TemplateEngine templateEngine, final Type type)
    {
        final Class<List<Object>> listClass = (Class<List<Object>>) templateEngine.getRawClassFromType(type);
        return templateEngine.instantiateClass(listClass);
    }
}
