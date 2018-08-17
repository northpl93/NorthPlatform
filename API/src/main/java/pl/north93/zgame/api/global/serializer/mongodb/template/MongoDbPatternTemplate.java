package pl.north93.zgame.api.global.serializer.mongodb.template;

import java.util.regex.Pattern;

import org.bson.BsonWriter;
import org.bson.codecs.PatternCodec;

import pl.north93.zgame.api.global.serializer.mongodb.MongoDbDeserializationContext;
import pl.north93.zgame.api.global.serializer.mongodb.MongoDbSerializationContext;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.template.Template;

public class MongoDbPatternTemplate implements Template<Pattern, MongoDbSerializationContext, MongoDbDeserializationContext>
{
    private static final PatternCodec codec = new PatternCodec();

    @Override
    public void serialise(final MongoDbSerializationContext context, final FieldInfo field, final Pattern object) throws Exception
    {
        final BsonWriter writer = context.getWriter();
        context.writeNameIfNeeded(field);

        codec.encode(writer, object, null);
    }

    @Override
    public Pattern deserialize(final MongoDbDeserializationContext context, final FieldInfo field) throws Exception
    {
        //context.readNameIfNeeded(field);
        //return codec.decode(context.getReader(), null);
        return null;
    }
}
