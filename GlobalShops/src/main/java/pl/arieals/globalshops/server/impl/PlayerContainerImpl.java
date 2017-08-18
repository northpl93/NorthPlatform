package pl.arieals.globalshops.server.impl;

import static java.text.MessageFormat.format;


import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;

import org.diorite.utils.lazy.LazyValue;

import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.shared.GroupType;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class PlayerContainerImpl implements IPlayerContainer
{
    @Inject
    private PlayerDataService     service;
    @Inject
    private GlobalShopsServer     shopsServer;
    private LazyValue<PlayerData> playerData;
    private final Player player;

    public PlayerContainerImpl(final Player player)
    {
        this.player = player;
        this.playerData = new LazyValue<>(() -> this.service.getData(player));
    }

    @Override
    public Player getBukkitPlayer()
    {
        return this.player;
    }

    @Override
    public Collection<Item> getBoughtItems(final ItemsGroup group)
    {
        final PlayerData data = this.playerData.get();
        return data.getBoughtItems().keySet().stream()
                   .filter(id -> StringUtils.startsWith(id, group.getId()))
                   .map(id -> this.getItemFromInternalId(group, id))
                   .collect(Collectors.toList());
    }

    @Override
    public boolean hasBoughtItem(final Item item)
    {
        final PlayerData data = this.playerData.get();
        return data.getBoughtItems().containsKey(this.itemToInternalId(item));
    }

    @Override
    public int getBoughtItemLevel(final Item item)
    {
        final PlayerData data = this.playerData.get();
        return data.getBoughtItems().getOrDefault(this.itemToInternalId(item), 0);
    }

    @Override
    public boolean hasMaxLevel(final Item item)
    {
        return this.getBoughtItemLevel(item) >= item.getMaxLevel();
    }

    @Override
    public Item getActiveItem(final ItemsGroup group)
    {
        if (group.getGroupType() == GroupType.MULTI_BUY)
        {
            throw new IllegalArgumentException();
        }

        final PlayerData data = this.playerData.get();
        final String activeItemId = data.getActiveItems().get(group.getId());
        if (activeItemId == null)
        {
            return null;
        }
        return this.shopsServer.getItem(group, activeItemId);
    }

    @Override
    public boolean addItem(final Item item, final int level)
    {
        Preconditions.checkNotNull(item, "Item can't be null");
        Preconditions.checkState(level > 0, "Level must be grater than 0.");
        Preconditions.checkState(item.getMaxLevel() <= level, "Level must be smaller or equal to item's max level.");

        final boolean success = this.service.addItem(this.player, item.getGroup().getId(), item.getId(), level);
        this.playerData.reset();

        return success;
    }

    @Override
    public void markAsActive(final Item item)
    {
        Preconditions.checkNotNull(item);

        final ItemsGroup group = item.getGroup();
        if (group.getGroupType() == GroupType.MULTI_BUY)
        {
            throw new IllegalArgumentException();
        }

        if (! this.hasBoughtItem(item))
        {
            throw new IllegalStateException(format("Item {0} isn't bought.", item.getId()));
        }

        this.service.setActiveItem(this.player, group.getId(), item.getId());
        this.playerData.reset();
    }

    @Override
    public void resetActiveItem(final ItemsGroup group)
    {
        Preconditions.checkNotNull(group);

        if (group.getGroupType() == GroupType.MULTI_BUY)
        {
            throw new IllegalArgumentException();
        }

        this.service.resetActiveItem(this.player, group.getId());
        this.playerData.reset();
    }

    private Item getItemFromInternalId(final ItemsGroup group, final String id)
    {
        final String properId = StringUtils.split(id, '$')[1];
        return this.shopsServer.getItem(group, properId);
    }

    private String itemToInternalId(final Item item)
    {
        return item.getGroup().getId() + "$" + item.getId();
    }
}
