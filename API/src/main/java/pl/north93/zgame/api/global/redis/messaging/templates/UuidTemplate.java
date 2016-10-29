package pl.north93.zgame.api.global.redis.messaging.templates;

import java.io.IOException;
import java.util.UUID;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class UuidTemplate implements Template<UUID>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final UUID object)
    {
        try
        {
            packer.packLong(object.getMostSignificantBits());
            packer.packLong(object.getLeastSignificantBits());
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public UUID deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker)
    {
        try
        {
            return new UUID(unpacker.unpackLong(), unpacker.unpackLong());
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to unpack UUID.", e);
        }
    }
}
