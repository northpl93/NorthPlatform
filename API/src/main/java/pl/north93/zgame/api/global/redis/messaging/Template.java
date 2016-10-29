package pl.north93.zgame.api.global.redis.messaging;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public interface Template<T>
{
    void serializeObject(TemplateManager templateManager, MessageBufferPacker packer, T object) throws Exception;

    T deserializeObject(TemplateManager templateManager, MessageUnpacker unpacker) throws Exception;
}
