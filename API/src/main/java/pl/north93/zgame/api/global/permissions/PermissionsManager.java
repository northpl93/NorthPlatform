package pl.north93.zgame.api.global.permissions;

import javax.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.ConfigUpdatedNetEvent;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;

public class PermissionsManager extends Component
{
    private final Logger logger = LoggerFactory.getLogger(PermissionsManager.class);
    @Inject @NetConfig(type = GroupsContainer.class, id = "groups")
    private IConfig<GroupsContainer> groups;
    private final Set<Group> cachedGroups = new HashSet<>();
    private Group            defaultGroup;

    @Override
    protected void enableComponent()
    {
        this.synchronizeGroups(this.groups.get());
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

    @NetEventSubscriber(ConfigUpdatedNetEvent.class)
    public void onConfigUpdated(final ConfigUpdatedNetEvent event)
    {
        if (! event.getConfigName().equals("groups"))
        {
            return;
        }

        this.synchronizeGroups(this.groups.get());
    }

    /**
     * Pobiera z Redisa listę grup i zapisuje ją w liście
     */
    private void synchronizeGroups(final @Nullable GroupsContainer groupsContainer)
    {
        if (groupsContainer == null)
        {
            this.logger.info("Skipped groups synchronization because config isn't loaded yet.");
            return;
        }

        // Fetched from redis. Now load it into List
        this.cachedGroups.clear();
        for (final GroupsContainer.GroupEntry groupEntry : groupsContainer.groups)
        {
            final Group group = new Group(groupEntry.name, groupEntry.chatFormat, groupEntry.joinMessage);
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
        this.logger.info("Loaded {} groups!", this.cachedGroups.size());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cachedGroups", this.cachedGroups).toString();
    }
}
