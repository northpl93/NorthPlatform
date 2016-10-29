package pl.north93.zgame.api.global.redis.messaging.templates;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class LongTemplate implements Template<Long>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Long object) throws Exception
    {
        try
        {
            packer.packLong(object);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Long deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        try
        {
            return unpacker.unpackLong();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to unpack long.", e);
        }
    }
}
