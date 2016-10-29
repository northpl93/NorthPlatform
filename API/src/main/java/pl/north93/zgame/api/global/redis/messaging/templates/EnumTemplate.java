package pl.north93.zgame.api.global.redis.messaging.templates;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class EnumTemplate implements Template<Enum>
{
    private final Class<?> enumClass;

    public EnumTemplate(final Class<?> enumClass)
    {
        this.enumClass = enumClass;
    }

    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Enum object)
    {
        try
        {
            packer.packInt(object.ordinal());
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Enum deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker)
    {
        try
        {
            return (Enum) this.enumClass.getEnumConstants()[unpacker.unpackInt()];
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Failed to unpack enum.", e);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enumClass", this.enumClass).toString();
    }
}
