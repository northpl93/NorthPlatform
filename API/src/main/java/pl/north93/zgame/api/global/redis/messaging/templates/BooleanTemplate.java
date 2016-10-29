package pl.north93.zgame.api.global.redis.messaging.templates;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class BooleanTemplate implements Template<Boolean>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Boolean object)
    {
        try
        {
            packer.packBoolean(object);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker)
    {
        try
        {
            return unpacker.unpackBoolean();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to unpack boolean.", e);
        }
    }
}
