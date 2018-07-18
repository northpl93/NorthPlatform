package pl.north93.zgame.api.global.redis.messaging.templates.extra;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class GroupInStringTemplate implements Template<Group>
{
    private final Logger logger = LoggerFactory.getLogger(GroupInStringTemplate.class);

    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Group object) throws Exception
    {
        packer.packString(object.getName());
    }

    @Override
    public Group deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final String name = unpacker.unpackString();
        final Group group = API.getApiCore().getPermissionsManager().getGroupByName(name);
        if (group == null)
        {
            this.logger.error("Unknown group name {}. Returning default group.", name);
            return API.getApiCore().getPermissionsManager().getDefaultGroup();
        }
        return group;
    }
}
