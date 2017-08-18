package pl.arieals.globalshops.server.impl;

import java.util.Map;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.observable.Value;

class PlayerDataService
{
    private final INetworkManager networkManager;

    @Bean
    private PlayerDataService(final INetworkManager networkManager)
    {
        this.networkManager = networkManager;
    }

    public PlayerData getData(final Player player)
    {
        final Value<IOnlinePlayer> value = this.networkManager.getPlayers().unsafe().getOnline(player.getName());

        final IOnlinePlayer onlinePlayer = value.get();
        if (onlinePlayer == null)
        {
            throw new IllegalStateException("Not found data of player "+ player.getName());
        }

        return new PlayerData(onlinePlayer.getMetaStore());
    }

    public boolean addItem(final Player player, final String groupId, final String itemId)
    {
        // przedmiot nie obsluguje poziomow, zgodnie z dokumentacja ustawiamy na 1.
        return this.addItem(player, itemId, groupId, 1);
    }

    public boolean addItem(final Player player, final String groupId, final String itemId, final Integer itemLevel)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(Identity.of(player)))
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

    public void setActiveItem(final Player player, final String groupId, final String itemId)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(Identity.of(player)))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());

            playerData.getActiveItems().put(groupId, itemId);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void resetActiveItem(final Player player, final String groupId)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(Identity.of(player)))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());

            playerData.getActiveItems().remove(groupId);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
