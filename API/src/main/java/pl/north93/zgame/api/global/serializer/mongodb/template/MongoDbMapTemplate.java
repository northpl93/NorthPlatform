package pl.north93.zgame.api.global.serializer.mongodb.template;

import java.lang.reflect.Type;
import java.util.Map;

import pl.north93.zgame.api.global.serializer.mongodb.MongoDbDeserializationContext;
import pl.north93.zgame.api.global.serializer.mongodb.MongoDbSerializationContext;
import pl.north93.zgame.api.global.serializer.platform.CustomFieldInfo;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public class MongoDbMapTemplate implements Template<Map<String, Object>, MongoDbSerializationContext, MongoDbDeserializationContext>
{
    @SuppressWarnings("unchecked")
    @Override
    public void serialise(final MongoDbSerializationContext context, final FieldInfo field, final Map object) throws Exception
    {
        context.enterObject(field);

        final Type genericType = this.getValueGenericType(context.getTemplateEngine(), field.getType());
        final Template<Object, SerializationContext, DeserializationContext> valueSerializer = context.getTemplateEngine().getTemplate(genericType);

        for (final Map.Entry<Object, Object> entry : ((Map<Object, Object>) object).entrySet())
        {
            final String keyName = entry.getKey().toString();
            final FieldInfo keyField = new CustomFieldInfo(keyName, genericType);

            valueSerializer.serialise(context, keyField, entry.getValue());
        }

        context.exitObject(field);
    }

    @Override
    public Map<String, Object> deserialize(final MongoDbDeserializationContext context, final FieldInfo field) throws Exception
    {
        try
        {
            context.enterObject(field); // wchodzimy do obiektu mapy

            final Type genericType = this.getValueGenericType(context.getTemplateEngine(), field.getType());
            final Template<Object, SerializationContext, DeserializationContext> valueSerializer = context.getTemplateEngine().getTemplate(genericType);

            final Map<String, Object> map = this.instantiateMap(context.getTemplateEngine(), field.getType());
            for (final String name : context.getKeys()) // zwraca liste kluczy w danym obiekcie, czyli naszej mapie
            {
                System.out.println("reading entry of map as " + valueSerializer);

                final FieldInfo keyField = new CustomFieldInfo(name, genericType);
                map.put(name, valueSerializer.deserialize(context, keyField));
            }

            return map;
        }
        finally
        {
            context.exitObject(field);
        }
    }

    protected Type getValueGenericType(final TemplateEngine templateEngine, final Type type)
    {
        return templateEngine.getTypeParameters(type)[1];
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> instantiateMap(final TemplateEngine engine, final Type type)
    {
        final Class<Map<String, Object>> mapClass = (Class<Map<String, Object>>) engine.getRawClassFromType(type);
        return engine.instantiateClass(mapClass);
    }
}
