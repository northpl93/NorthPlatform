package pl.north93.zgame.api.global.serializer.platform.template.builtin;

import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;

public class BooleanTemplate implements Template<Boolean, SerializationContext, DeserializationContext>
{
    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Boolean object) throws Exception
    {
        context.writeBoolean(field, object);
    }

    @Override
    public Boolean deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        return context.readBoolean(field);
    }
}
