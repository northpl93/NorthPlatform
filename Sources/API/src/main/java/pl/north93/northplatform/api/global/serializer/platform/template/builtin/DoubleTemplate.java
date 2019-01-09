package pl.north93.northplatform.api.global.serializer.platform.template.builtin;

import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;

public class DoubleTemplate implements Template<Double, SerializationContext, DeserializationContext>
{
    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Double object) throws Exception
    {
        context.writeDouble(field, object);
    }

    @Override
    public Double deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        return context.readDouble(field);
    }
}
