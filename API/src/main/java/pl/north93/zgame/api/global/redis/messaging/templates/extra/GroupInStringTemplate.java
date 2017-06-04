package pl.north93.zgame.api.global.redis.messaging.templates.extra;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class GroupInStringTemplate implements Template<Group>
{
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
            API.getLogger().severe("[GroupInStringTemplate] Unknown group name " + name + ". Returning default group.");
            return API.getApiCore().getPermissionsManager().getDefaultGroup();
        }
        return group;
    }
}
