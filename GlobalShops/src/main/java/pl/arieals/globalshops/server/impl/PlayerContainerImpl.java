package pl.arieals.globalshops.server.impl;

import static java.text.MessageFormat.format;


import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import org.diorite.utils.lazy.LazyValue;

import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.event.ItemMarkedActiveEvent;
import pl.arieals.globalshops.shared.GroupType;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class PlayerContainerImpl implements IPlayerContainer
{
    @Inject
    private PlayerDataService service;
    @Inject
    private GlobalShopsServer shopsServer;
    private PlayerDataHolder  playerData;
    private final Player player;

    public PlayerContainerImpl(final Player player)
    {
        this.player = player;
        this.playerData = new PlayerDataHolder(() -> this.service.getData(player));
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
    public Map<Item, Integer> getBoughtItemsLevel(ItemsGroup group)
    {
    	// TODO;
    	return null;
    }

    @Override
    public boolean hasBoughtItem(final Item item)
    {
        final PlayerData data = this.playerData.get();
        return data.getBoughtItems().containsKey(this.itemToInternalId(item));
    }
    
    @Override
    public boolean hasBoughtItemAtLevel(Item item, int level)
    {
    	return getBoughtItemLevel(item) >= level;
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
        Preconditions.checkState(level <= item.getMaxLevel(), "Level must be smaller or equal to item's max level.");

        final boolean success = this.service.addItem(this.player, item.getGroup().getId(), item.getId(), level);
        this.playerData.reset();

        return success;
    }

    @Override
    public boolean bumpItemLevel(final Item item)
    {
        if (this.hasMaxLevel(item))
        {
            return false;
        }

        final int currentLevel = this.getBoughtItemLevel(item);
        return this.addItem(item, currentLevel + 1);
    }

    @Override
    public void addShards(final Item item, final int amount)
    {
        Preconditions.checkNotNull(item, "Item can't be null");
        Preconditions.checkArgument(amount > 0);
        Preconditions.checkArgument(amount <= 100);

        final Pair<PlayerData, Integer> result = this.service.addShards(this.player, item.getGroup().getId(), item.getId(), amount);

        final int newShards = result.getValue();
        if (newShards >= 100)
        {
            this.bumpItemLevel(item);

            final PlayerData newData;
            if (this.hasMaxLevel(item))
            {
                newData = this.service.setShards(this.player, item.getGroup().getId(), item.getId(), 0);
            }
            else
            {
                newData = this.service.setShards(this.player, item.getGroup().getId(), item.getId(), newShards - 100);
            }

            this.playerData.update(newData);
        }
        else
        {
            // aktualizujemy danymi pobranymi z add shards
            this.playerData.update(result.getKey());
        }
    }

    @Override
    public int getShards(final Item item)
    {
        final PlayerData playerData = this.playerData.get();

        return playerData.getShards().getOrDefault(this.itemToInternalId(item), 0);
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

        final PlayerData updatedData = this.service.setActiveItem(this.player, group.getId(), item.getId());
        this.playerData.update(updatedData);

        this.player.getServer().getPluginManager().callEvent(new ItemMarkedActiveEvent(this.player, this, group, item));
    }

    @Override
    public void resetActiveItem(final ItemsGroup group)
    {
        Preconditions.checkNotNull(group);

        if (group.getGroupType() == GroupType.MULTI_BUY)
        {
            throw new IllegalArgumentException();
        }

        final PlayerData updatedData = this.service.resetActiveItem(this.player, group.getId());
        this.playerData.update(updatedData);

        this.player.getServer().getPluginManager().callEvent(new ItemMarkedActiveEvent(this.player, this, group, null));
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

class PlayerDataHolder extends LazyValue<PlayerData>
{
    public PlayerDataHolder(final Supplier<PlayerData> supplier)
    {
        super(supplier);
    }

    public void update(final PlayerData playerData)
    {
        this.cached = playerData;
        this.isCached = true;
    }
}