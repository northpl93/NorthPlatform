package pl.north93.northplatform.api.global.metadata;

import pl.north93.serializer.platform.context.DeserializationContext;
import pl.north93.serializer.platform.context.SerializationContext;
import pl.north93.serializer.platform.template.Template;
import pl.north93.serializer.platform.template.field.FieldInfo;

public class MetaKeyTemplate implements Template<MetaKey, SerializationContext, DeserializationContext>
{
    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final MetaKey object) throws Exception
    {
        context.writeString(field, object.getKey());
    }

    @Override
    public MetaKey deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        return MetaKey.get(context.readString(field));
    }
}
