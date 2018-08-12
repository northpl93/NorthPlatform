package pl.north93.zgame.api.global.serializer.platform;

import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public interface SerializationFormat<OUTPUT>
{
    void configure(TemplateEngine templateEngine);

    SerializationContext createSerializationContext(TemplateEngine templateEngine);

    DeserializationContext createDeserializationContext(TemplateEngine templateEngine, OUTPUT serializedData);
}
