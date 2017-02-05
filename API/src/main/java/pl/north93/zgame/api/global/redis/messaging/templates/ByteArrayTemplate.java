package pl.north93.zgame.api.global.redis.messaging.templates;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class ByteArrayTemplate implements Template<byte[]>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final byte[] object) throws Exception
    {
        packer.packArrayHeader(object.length);
        packer.writePayload(object);
    }

    @Override
    public byte[] deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final int arraySize = unpacker.unpackArrayHeader();
        return unpacker.readPayload(arraySize);
    }
}
