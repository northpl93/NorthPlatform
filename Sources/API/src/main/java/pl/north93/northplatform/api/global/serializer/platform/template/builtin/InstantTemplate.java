package pl.north93.northplatform.api.global.serializer.platform.template.builtin;

import java.time.Instant;

import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;
import pl.north93.northplatform.api.global.serializer.platform.CustomFieldInfo;

public class InstantTemplate implements Template<Instant, SerializationContext, DeserializationContext>
{
    private static final FieldInfo FIELD_EPOCH_SECOND    = new CustomFieldInfo("epochSecond", Long.class);
    private static final FieldInfo FIELD_NANO_ADJUSTMENT = new CustomFieldInfo("nanoAdjustment", Integer.class);

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Instant object) throws Exception
    {
        context.enterObject(field);

        context.writeLong(FIELD_EPOCH_SECOND, object.getEpochSecond());
        context.writeInteger(FIELD_NANO_ADJUSTMENT, object.getNano());

        context.exitObject(field);
    }

    @Override
    public Instant deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        try
        {
            context.enterObject(field);

            final long epochSecond = context.readLong(FIELD_EPOCH_SECOND);
            final int nanoAdjustment = context.readInteger(FIELD_NANO_ADJUSTMENT);
            return Instant.ofEpochSecond(epochSecond, nanoAdjustment);
        }
        finally
        {
            context.exitObject(field);
        }
    }
}
