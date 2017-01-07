package pl.north93.zgame.api.global.redis.messaging.templates;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class FloatTemplate implements Template<Float>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Float object) throws Exception
    {
        packer.packFloat(object);
    }

    @Override
    public Float deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        return unpacker.unpackFloat();
    }
}
