package pl.north93.zgame.api.global.redis.messaging.templates.extra;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class ServerPatternInStringTemplate implements Template<ServerPattern>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final ServerPattern object) throws Exception
    {
        packer.packString(object.getPatternName());
    }

    @Override
    public ServerPattern deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final String message = unpacker.unpackString();
        return API.getNetworkManager().getServerPattern(message);
    }
}
