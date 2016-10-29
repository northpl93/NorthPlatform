package pl.north93.zgame.api.global.redis.messaging.templates;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class IntegerTemplate implements Template<Integer>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Integer object)
    {
        try
        {
            packer.packInt(object);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Integer deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker)
    {
        try
        {
            return unpacker.unpackInt();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to unpack integer.", e);
        }
    }
}
