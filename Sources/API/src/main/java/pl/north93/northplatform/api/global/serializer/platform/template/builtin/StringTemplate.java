package pl.north93.northplatform.api.global.serializer.platform.template.builtin;

import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;

public class StringTemplate implements Template<String, SerializationContext, DeserializationContext>
{
    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final String object) throws Exception
    {
        context.writeString(field, object);
    }

    @Override
    public String deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        return context.readString(field);
    }
}
