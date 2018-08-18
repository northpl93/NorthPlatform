package pl.arieals.globalshops.server.impl;

import static java.text.MessageFormat.format;


import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import org.diorite.commons.lazy.LazyValue;

import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.domain.Item;
import pl.arieals.globalshops.server.domain.ItemsGroup;
import pl.arieals.globalshops.server.event.ItemMarkedActiveEvent;
import pl.arieals.globalshops.shared.GroupType;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class PlayerContainerImpl implements IPlayerContainer
{
    @Inject
    private       PlayerShopDataService service;
    @Inject
    private       GlobalShopsServer     shopsServer;
    private       PlayerDataHolder      playerData;
    private final INorthPlayer          player;

    public PlayerContainerImpl(final INorthPlayer player)
    {
        this.player = player;
        this.playerData = new PlayerDataHolder(() -> this.service.getData(player));
    }

    @Override
    public INorthPlayer getBukkitPlayer()
    {
        return this.player;
    }

    @Override
    public Collection<Item> getBoughtItems(final ItemsGroup group)
    {
        final PlayerShopData data = this.getData();
        return data.getItems().stream()
                   .filter(itemInfo -> itemInfo.isBought() && itemInfo.getGroupId().equals(group.getId()))
                   .map(itemInfo -> this.getItemFromId(group, itemInfo.getItemId()))
                   .collect(Collectors.toList());
    }
    
    @Override
    public Map<Item, Integer> getBoughtItemsLevel(final ItemsGroup group)
    {
        final PlayerShopData data = this.getData();
        return data.getItems().stream().collect(Collectors.toMap(item -> this.getItemFromId(group, item.getItemId()), PlayerItemInfo::getBoughtLevel));
    }

    @Override
    public boolean hasBoughtItem(final Item item)
    {
        final PlayerShopData data = this.getData();

        final String groupId = item.getGroup().getId();
        return data.getOptionalItemInfo(groupId, item.getId())
                   .map(PlayerItemInfo::isBought)
                   .orElse(false);
    }
    
    @Override
    public boolean hasBoughtItemAtLevel(final Item item, final int level)
    {
    	return this.getBoughtItemLevel(item) >= level;
    }

    @Override
    public int getBoughtItemLevel(final Item item)
    {
        final PlayerShopData data = this.getData();

        final String groupId = item.getGroup().getId();
        return data.getOptionalItemInfo(groupId, item.getId())
                   .map(PlayerItemInfo::getBoughtLevel)
                   .orElse(0);
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

        final PlayerShopData data = this.getData();
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

        final Pair<PlayerShopData, Integer> result = this.service.addShards(this.player, item.getGroup().getId(), item.getId(), amount);

        final int newShards = result.getValue();
        if (newShards >= 100)
        {
            final PlayerShopData newData;
            if (this.bumpItemLevel(item))
            {
                if (this.hasMaxLevel(item))
                {
                    newData = this.service.setShards(this.player, item.getGroup().getId(), item.getId(), 0);
                }
                else
                {
                    newData = this.service.setShards(this.player, item.getGroup().getId(), item.getId(), newShards - 100);
                }
            }
            else
            {
                // gracz bedzie mial ponad 100 shardów, ale to i tak chyba najlepszy sposób wyjscia
                // z tej sytuacji.
                newData = result.getKey();
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
        final PlayerShopData playerData = this.getData();

        final String groupId = item.getGroup().getId();
        return playerData.getOptionalItemInfo(groupId, item.getId())
                         .map(PlayerItemInfo::getShards)
                         .orElse(0);
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

        final PlayerShopData updatedData = this.service.setActiveItem(this.player, group.getId(), item.getId());
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

        final PlayerShopData updatedData = this.service.resetActiveItem(this.player, group.getId());
        this.playerData.update(updatedData);

        this.player.getServer().getPluginManager().callEvent(new ItemMarkedActiveEvent(this.player, this, group, null));
    }

    @Nonnull
    private PlayerShopData getData()
    {
        //noinspection ConstantConditions Tu nigdy nie bedzie nulla
        return this.playerData.get();
    }

    private Item getItemFromId(final ItemsGroup group, final String id)
    {
        return this.shopsServer.getItem(group, id);
    }

    private Item getItemFromInternalId(final ItemsGroup group, final String id)
    {
        final String properId = StringUtils.split(id, '$')[1];
        return this.getItemFromId(group, properId);
    }

    private String itemToInternalId(final Item item)
    {
        return item.getGroup().getId() + "$" + item.getId();
    }
}

class PlayerDataHolder extends LazyValue<PlayerShopData>
{
    public PlayerDataHolder(final Supplier<PlayerShopData> supplier)
    {
        super(supplier);
    }

    public void update(final PlayerShopData playerData)
    {
        this.cached = playerData;
        this.isCached = true;
    }
}