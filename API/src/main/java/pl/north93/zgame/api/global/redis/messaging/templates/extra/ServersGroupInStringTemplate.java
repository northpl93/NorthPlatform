package pl.north93.zgame.api.global.redis.messaging.templates.extra;

import org.apache.commons.lang3.StringUtils;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class ServersGroupInStringTemplate implements Template<ServersGroup>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final ServersGroup object) throws Exception
    {
        if (object == null)
        {
            packer.packString("");
        }
        else
        {
            packer.packString(object.getName());
        }
    }

    @Override
    public ServersGroup deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final String message = unpacker.unpackString();
        if (StringUtils.isEmpty(message))
        {
            return null;
        }
        else
        {
            return API.getNetworkManager().getServersGroup(message);
        }
    }
}
