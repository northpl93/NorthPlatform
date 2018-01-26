package pl.north93.zgame.api.global.redis.messaging.templates;

import java.time.Duration;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class DurationTemplate implements Template<Duration>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Duration object) throws Exception
    {
        packer.packLong(object.getSeconds());
        packer.packInt(object.getNano());
    }

    @Override
    public Duration deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        return Duration.ofSeconds(unpacker.unpackLong(), unpacker.unpackInt());
    }
}
