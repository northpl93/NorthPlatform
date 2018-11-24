package pl.north93.northplatform.api.global.serializer.platform.template.builtin;

import java.time.Duration;

import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;
import pl.north93.northplatform.api.global.serializer.platform.CustomFieldInfo;

public class DurationTemplate implements Template<Duration, SerializationContext, DeserializationContext>
{
    private static final FieldInfo FIELD_SECONDS         = new CustomFieldInfo("seconds", Long.class);
    private static final FieldInfo FIELD_NANO_ADJUSTMENT = new CustomFieldInfo("nanoAdjustment", Integer.class);

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Duration object) throws Exception
    {
        context.enterObject(field);

        context.writeLong(FIELD_SECONDS, object.getSeconds());
        context.writeInteger(FIELD_NANO_ADJUSTMENT, object.getNano());

        context.exitObject(field);
    }

    @Override
    public Duration deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        try
        {
            context.enterObject(field);

            final long seconds = context.readLong(FIELD_SECONDS);
            final int nanoAdjustment = context.readInteger(FIELD_NANO_ADJUSTMENT);
            return Duration.ofSeconds(seconds, nanoAdjustment);
        }
        finally
        {
            context.exitObject(field);
        }
    }
}
