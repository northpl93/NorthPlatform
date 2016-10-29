package pl.north93.zgame.api.global.redis.messaging.templates;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class ShortTemplate implements Template<Short>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Short object)
    {
        try
        {
            packer.packShort(object);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Short deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker)
    {
        try
        {
            return unpacker.unpackShort();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to unpack short.", e);
        }
    }
}
