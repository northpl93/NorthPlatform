package pl.north93.zgame.api.global.redis.messaging.templates;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class DoubleTemplate implements Template<Double>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Double object) throws Exception
    {
        packer.packDouble(object);
    }

    @Override
    public Double deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        return unpacker.unpackDouble();
    }
}
