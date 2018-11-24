package pl.north93.northplatform.api.global.serializer.mongodb;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.types.ObjectId;

import pl.north93.northplatform.api.global.serializer.mongodb.template.MongoDbListTemplate;
import pl.north93.northplatform.api.global.serializer.mongodb.template.MongoDbMapTemplate;
import pl.north93.northplatform.api.global.serializer.mongodb.template.MongoDbUuidTemplate;
import pl.north93.northplatform.api.global.serializer.mongodb.template.MongoDbDocumentTemplate;
import pl.north93.northplatform.api.global.serializer.mongodb.template.MongoDbObjectIdTemplate;
import pl.north93.northplatform.api.global.serializer.mongodb.template.MongoDbPatternTemplate;
import pl.north93.northplatform.api.global.serializer.platform.SerializationFormat;
import pl.north93.northplatform.api.global.serializer.platform.TypePredictor;
import pl.north93.northplatform.api.global.serializer.platform.template.AnyInheritedTypeFilter;
import pl.north93.northplatform.api.global.serializer.platform.template.ExactTypeIgnoreGenericFilter;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplatePriority;

public class MongoDbSerializationFormat implements SerializationFormat<BsonReader, MongoDbSerializationContext, MongoDbDeserializationContext>
{
    @Override
    public void configure(final TemplateEngine templateEngine)
    {
        templateEngine.register(new ExactTypeIgnoreGenericFilter(UUID.class), new MongoDbUuidTemplate());
        templateEngine.register(new ExactTypeIgnoreGenericFilter(ObjectId.class), new MongoDbObjectIdTemplate());
        templateEngine.register(new ExactTypeIgnoreGenericFilter(Pattern.class), new MongoDbPatternTemplate());

        templateEngine.register(new AnyInheritedTypeFilter(List.class), new MongoDbListTemplate());

        templateEngine.register(new AnyInheritedTypeFilter(Map.class), new MongoDbMapTemplate());
        // dla Document musimy miec specjalna templatke bo inaczej posypie sie przez brak generic type
        templateEngine.register(new ExactTypeIgnoreGenericFilter(Document.class, TemplatePriority.HIGHEST), new MongoDbDocumentTemplate());
    }

    @Override
    public MongoDbSerializationContext createSerializationContext(final TemplateEngine templateEngine)
    {
        final BsonWriter writer = MongoDbCodec.writer.get();
        return new MongoDbSerializationContext(templateEngine, writer);
    }

    @Override
    public MongoDbDeserializationContext createDeserializationContext(final TemplateEngine templateEngine, final BsonReader serializedData)
    {
        return new MongoDbDeserializationContext(templateEngine, serializedData);
    }

    @Nullable
    @Override
    public TypePredictor<MongoDbSerializationContext, MongoDbDeserializationContext> getTypePredictor()
    {
        return new MongoDbTypePredictor();
    }
}
