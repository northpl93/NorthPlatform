package pl.north93.zgame.api.global.serializer.platform;

import javax.annotation.Nullable;

import java.lang.reflect.Type;

import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public interface TypePredictor<SERIALIZATION_CONTEXT extends SerializationContext, DESERIALIZATION_CONTEXT extends DeserializationContext>
{
    boolean isTypePredictable(TemplateEngine templateEngine, Type type);

    @Nullable
    Template<Object, SERIALIZATION_CONTEXT, DESERIALIZATION_CONTEXT> predictType(DESERIALIZATION_CONTEXT deserializationContext, FieldInfo field);
}
