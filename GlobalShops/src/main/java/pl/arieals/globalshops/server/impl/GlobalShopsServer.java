package pl.arieals.globalshops.server.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.controller.cfg.ItemsGroupCfg;
import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsDataContainer;
import pl.arieals.globalshops.shared.ItemsGroup;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.ConfigUpdatedNetEvent;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;
import pl.north93.zgame.api.global.utils.lang.CollectionUtils;

public class GlobalShopsServer extends Component implements IGlobalShops
{
    @Inject @NetConfig(type = ItemsDataContainer.class, id = "globalShops")
    private IConfig<ItemsDataContainer> config;
    private final Map<String, ItemsGroup> groups = new HashMap<>();

    @Override
    protected void enableComponent()
    {
        final ItemsDataContainer itemsDataContainer = this.config.get();
        if (itemsDataContainer == null)
        {
            this.getLogger().warning("[GlobalShops] Couldn't fetch items data from network. Did the network controller is setup properly?");
            return;
        }
        this.updateGroups0(itemsDataContainer);
    }

    @Override
    protected void disableComponent()
    {
    }

    @NetEventSubscriber(ConfigUpdatedNetEvent.class)
    private void updateGroups(final ConfigUpdatedNetEvent event)
    {
        if (! event.getConfigName().equals("globalShops"))
        {
            return;
        }

        this.getLogger().info("[GlobalShops] Updating items config.");
        this.updateGroups0(this.config.get());
    }

    private synchronized void updateGroups0(final ItemsDataContainer container)
    {
        this.groups.clear();
        for (final ItemsGroupCfg itemsGroupCfg : container.getGroups())
        {
            final ItemsGroup itemsGroup = new ItemsGroup(itemsGroupCfg);
            this.groups.put(itemsGroup.getId(), itemsGroup);
        }
    }

    @Override
    public synchronized ItemsGroup getGroup(final String id)
    {
        Preconditions.checkNotNull(id);
        return this.groups.get(id);
    }

    @Override
    public synchronized Item getItem(final ItemsGroup group, final String id)
    {
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(id);
        final Item result = CollectionUtils.findInCollection(group.getItems(), Item::getId, id);
        if (result != null)
        {
            return result;
        }
        throw new IllegalArgumentException("Not found item with ID " + id);
    }

    @Override
    public IPlayerContainer getPlayer(final Player player)
    {
        Preconditions.checkNotNull(player);
        return new PlayerContainerImpl(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("groups", this.groups).toString();
    }
}
