package pl.arieals.globalshops.server.impl;

import java.util.Map;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.observable.Value;

class PlayerDataService
{
    private final IPlayersManager playersManager;

    @Bean
    private PlayerDataService(final IPlayersManager playersManager)
    {
        this.playersManager = playersManager;
    }

    public PlayerData getData(final Player player)
    {
        final Value<IOnlinePlayer> value = this.playersManager.unsafe().getOnlineValue(player.getName());

        final IOnlinePlayer onlinePlayer = value.get();
        if (onlinePlayer == null)
        {
            throw new IllegalStateException("Not found data of player "+ player.getName());
        }

        return new PlayerData(onlinePlayer.getMetaStore());
    }

    public boolean addItem(final Player player, final String groupId, final String itemId, final Integer itemLevel)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());
            final String itemInternalId = groupId + "$" + itemId;

            final Map<String, Integer> boughtItems = playerData.getBoughtItems();

            final Integer actualLevel = boughtItems.getOrDefault(itemInternalId, 0);
            if (actualLevel >= itemLevel)
            {
                return false;
            }

            boughtItems.put(itemInternalId, itemLevel);
            return true;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public PlayerData setActiveItem(final Player player, final String groupId, final String itemId)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());

            playerData.getActiveItems().put(groupId, itemId);

            return playerData;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public PlayerData resetActiveItem(final Player player, final String groupId)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());

            playerData.getActiveItems().remove(groupId);

            return playerData;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Pair<PlayerData, Integer> addShards(final Player player, final String groupId, final String itemId, final int shards)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());
            final String itemInternalId = groupId + "$" + itemId;

            final Map<String, Integer> shardsMap = playerData.getShards();

            final int actualShards = shardsMap.getOrDefault(itemInternalId, 0);
            final int newShards = actualShards + shards;

            shardsMap.put(itemInternalId, newShards);

            return Pair.of(playerData, newShards);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public PlayerData setShards(final Player player, final String groupId, final String itemId, final int shards)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(Identity.of(player)))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());
            final String itemInternalId = groupId + "$" + itemId;

            final Map<String, Integer> shardsMap = playerData.getShards();
            if (shards == 0)
            {
                shardsMap.remove(itemInternalId);
            }
            else
            {
                shardsMap.put(itemInternalId, shards);
            }

            return playerData;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
