package pl.north93.zgame.api.global.redis.messaging.templates.extra;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class ServersGroupInStringTemplate implements Template<IServersGroup>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final IServersGroup object) throws Exception
    {
        packer.packString(object.getName());
    }

    @Override
    public IServersGroup deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final String message = unpacker.unpackString();
        return API.getNetworkManager().getServers().getServersGroup(message);
    }
}
