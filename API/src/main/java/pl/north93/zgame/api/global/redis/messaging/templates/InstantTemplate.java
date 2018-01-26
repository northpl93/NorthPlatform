package pl.north93.zgame.api.global.redis.messaging.templates;

import java.time.Instant;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class InstantTemplate implements Template<Instant>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Instant object) throws Exception
    {
        packer.packLong(object.getEpochSecond());
        packer.packInt(object.getNano());
    }

    @Override
    public Instant deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        return Instant.ofEpochSecond(unpacker.unpackLong(), unpacker.unpackInt());
    }
}
