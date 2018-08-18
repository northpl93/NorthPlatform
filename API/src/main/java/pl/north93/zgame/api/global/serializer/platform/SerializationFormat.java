package pl.north93.zgame.api.global.serializer.platform;

import javax.annotation.Nullable;

import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public interface SerializationFormat<OUTPUT, SERIALIZATION_CONTEXT extends SerializationContext, DESERIALIZATION_CONTEXT extends DeserializationContext>
{
    void configure(TemplateEngine templateEngine);

    SERIALIZATION_CONTEXT createSerializationContext(TemplateEngine templateEngine);

    DESERIALIZATION_CONTEXT createDeserializationContext(TemplateEngine templateEngine, OUTPUT serializedData);
    
    @Nullable
    TypePredictor<SERIALIZATION_CONTEXT, DESERIALIZATION_CONTEXT> getTypePredictor();
}
