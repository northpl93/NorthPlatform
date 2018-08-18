package pl.arieals.globalshops.server.impl;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.observable.Value;

class PlayerShopDataService
{
    private static final MetaKey SHOP_DATA = MetaKey.get("shopData");
    private final IPlayersManager playersManager;

    @Bean
    private PlayerShopDataService(final IPlayersManager playersManager)
    {
        this.playersManager = playersManager;
    }

    public PlayerShopData getData(final Player player)
    {
        final Value<IOnlinePlayer> value = this.playersManager.unsafe().getOnlineValue(player.getName());

        final IOnlinePlayer onlinePlayer = value.get();
        if (onlinePlayer == null)
        {
            throw new IllegalStateException("Not found data of player "+ player.getName());
        }

        return this.getData0(onlinePlayer);
    }

    private PlayerShopData getData0(final IPlayer player)
    {
        final MetaStore metaStore = player.getMetaStore();
        if (metaStore.contains(SHOP_DATA))
        {
            return metaStore.get(MetaKey.get("shopData"));
        }

        final PlayerShopData playerData = new PlayerShopData();
        metaStore.set(SHOP_DATA, playerData);

        return playerData;
    }

    public boolean addItem(final Player player, final String groupId, final String itemId, final Integer itemLevel)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerShopData playerData = this.getData0(t.getPlayer());

            final PlayerItemInfo itemInfo = playerData.getItemInfo(groupId, itemId);
            if (itemInfo.getBoughtLevel() >= itemLevel)
            {
                return false;
            }

            itemInfo.setBoughtLevel(itemLevel);
            return true;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public PlayerShopData setActiveItem(final Player player, final String groupId, final String itemId)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerShopData playerData = this.getData0(t.getPlayer());

            playerData.getActiveItems().put(groupId, itemId);

            return playerData;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public PlayerShopData resetActiveItem(final Player player, final String groupId)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerShopData playerData = this.getData0(t.getPlayer());

            playerData.getActiveItems().remove(groupId);

            return playerData;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Pair<PlayerShopData, Integer> addShards(final Player player, final String groupId, final String itemId, final int shards)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerShopData playerData = this.getData0(t.getPlayer());
            final PlayerItemInfo itemInfo = playerData.getItemInfo(groupId, itemId);

            final int newShards = itemInfo.getShards() + shards;
            itemInfo.setShards(newShards);

            return Pair.of(playerData, newShards);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public PlayerShopData setShards(final Player player, final String groupId, final String itemId, final int shards)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerShopData playerData = this.getData0(t.getPlayer());
            final PlayerItemInfo itemInfo = playerData.getItemInfo(groupId, itemId);

            itemInfo.setShards(shards);
            return playerData;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
