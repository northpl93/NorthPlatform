package pl.north93.northplatform.api.global.serializer.platform.template.builtin;

import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;

public class ByteTemplate implements Template<Byte, SerializationContext, DeserializationContext>
{
    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Byte object) throws Exception
    {
        context.writeByte(field, object);
    }

    @Override
    public Byte deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        return context.readByte(field);
    }
}
