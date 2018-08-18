package pl.north93.zgame.api.global.serializer.platform;

import java.lang.reflect.Type;

import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public interface NorthSerializer<OUTPUT>
{
    OUTPUT serialize(Type type, Object object);

    default OUTPUT serialize(final Object object)
    {
        return this.serialize(object.getClass(), object);
    }

    <T> T deserialize(Type type, OUTPUT serialized);

    SerializationFormat<OUTPUT, ? extends SerializationContext, ? extends DeserializationContext> getSerializationFormat();

    TemplateEngine getTemplateEngine();
}
