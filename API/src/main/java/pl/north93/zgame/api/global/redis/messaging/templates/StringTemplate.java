package pl.north93.zgame.api.global.redis.messaging.templates;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class StringTemplate implements Template<String>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final String object)
    {
        try
        {
            packer.packString(object);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker)
    {
        try
        {
            return unpacker.unpackString();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to unpack string.", e);
        }
    }
}
