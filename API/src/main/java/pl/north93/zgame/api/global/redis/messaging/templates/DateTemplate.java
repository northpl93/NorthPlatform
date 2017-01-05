package pl.north93.zgame.api.global.redis.messaging.templates;

import java.util.Date;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class DateTemplate implements Template<Date>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Date object) throws Exception
    {
        packer.packLong(object.getTime());
    }

    @Override
    public Date deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        return new Date(unpacker.unpackLong());
    }
}
