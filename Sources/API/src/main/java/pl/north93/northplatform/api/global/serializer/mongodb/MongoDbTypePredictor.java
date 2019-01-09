package pl.north93.northplatform.api.global.serializer.mongodb;

import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.BsonType;
import org.bson.types.ObjectId;

import pl.north93.northplatform.api.global.serializer.platform.TypePredictor;
import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;

public class MongoDbTypePredictor implements TypePredictor<MongoDbSerializationContext, MongoDbDeserializationContext>
{
    private final Map<BsonType, Class<?>> mappings = new IdentityHashMap<>();

    public MongoDbTypePredictor()
    {
        this.mappings.put(BsonType.STRING, String.class);
        this.mappings.put(BsonType.BOOLEAN, Boolean.class);
        this.mappings.put(BsonType.INT64, Long.class);
        this.mappings.put(BsonType.OBJECT_ID, ObjectId.class);
        this.mappings.put(BsonType.REGULAR_EXPRESSION, Pattern.class);
    }

    @Override
    public boolean isTypePredictable(final TemplateEngine templateEngine, final Type type)
    {
        //noinspection SuspiciousMethodCalls proste typy które tu chcemy będą Class<?>'ami
        return this.mappings.values().contains(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Template<Object, MongoDbSerializationContext, MongoDbDeserializationContext> predictType(final MongoDbDeserializationContext deserializationContext, final FieldInfo field)
    {
        final BsonType type = deserializationContext.readType(field);

        final Class<?> clazz = this.mappings.get(type);
        if (clazz == null)
        {
            return null;
        }

        final TemplateEngine templateEngine = deserializationContext.getTemplateEngine();
        return (Template) templateEngine.getTemplate(clazz);
    }
}
