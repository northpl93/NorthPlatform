package pl.north93.zgame.api.global.permissions;

import static pl.north93.zgame.api.global.redis.RedisKeys.PERMISSIONS_GROUPS;


import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.messages.GroupsContainer;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import redis.clients.jedis.Jedis;

public class PermissionsManager extends Component
{
    private final Set<Group> cachedGroups = new ObjectArraySet<>();
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector  storageConnector;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private TemplateManager   msgPack;
    private Group             defaultGroup;

    @Override
    protected void enableComponent()
    {
        this.synchronizeGroups();
    }

    @Override
    protected void disableComponent()
    {
    }

    public Group getGroupByName(final String name)
    {
        for (final Group cachedGroup : this.cachedGroups)
        {
            if (cachedGroup.getName().equals(name))
            {
                return cachedGroup;
            }
        }
        return null;
    }

    public Group getDefaultGroup()
    {
        return this.defaultGroup;
    }

    /**
     * Pobiera z Redisa listę grup i zapisuje ją w liście
     */
    public void synchronizeGroups()
    {
        this.getApiCore().getLogger().info("Synchronizing groups...");
        final GroupsContainer groupsContainer;
        try (final Jedis jedis = this.storageConnector.getJedisPool().getResource())
        {
            if (! jedis.exists(PERMISSIONS_GROUPS))
            {
                this.getApiCore().getLogger().warning("Key " + PERMISSIONS_GROUPS + " doesn't exist! Synchronization skipped...");
                return;
            }

            final byte[] msgPackGroups = jedis.get(PERMISSIONS_GROUPS.getBytes());
            groupsContainer = this.msgPack.deserialize(GroupsContainer.class, msgPackGroups);
        }
        // Fetched from redis. Now load it into List
        this.cachedGroups.clear();
        for (final GroupsContainer.GroupEntry groupEntry : groupsContainer.groups)
        {
            final Group group = new Group(groupEntry.name);
            group.setChatFormat(groupEntry.chatFormat);
            group.setJoinMessage(groupEntry.joinMessage);
            groupEntry.permissions.forEach(group::addPermission);
            this.cachedGroups.add(group);
        }
        // All groups populated. Now link the inheritance.
        for (final GroupsContainer.GroupEntry groupEntry : groupsContainer.groups)
        {
            if (groupEntry.inheritance.isEmpty())
            {
                continue; // Skip groups without inherit
            }

            final Group group = this.getGroupByName(groupEntry.name);
            for (final String inheritGroup : groupEntry.inheritance)
            {
                group.addInheritGroup(this.getGroupByName(inheritGroup));
            }
        }
        this.defaultGroup = this.getGroupByName(groupsContainer.defaultGroup);
        this.getApiCore().getLogger().info("Loaded " + this.cachedGroups.size() + " groups!");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cachedGroups", this.cachedGroups).toString();
    }
}
