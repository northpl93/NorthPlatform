package pl.arieals.globalshops.server.impl;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;

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
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(player.getUniqueId()))
        {
            return new PlayerData(t.getPlayer().getMetaStore());
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addItem(final Player player, final String itemId)
    {
        // przedmiot nie obsluguje poziomow, zgodnie z dokumentacja ustawiamy na 1.
        this.addItem(player, itemId, 1);
    }

    public void addItem(final Player player, final String itemId, final Integer itemLevel)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(player.getUniqueId()))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());

            playerData.getBoughtItems().put(itemId, itemLevel);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setActiveItem(final Player player, final String groupId, final String itemId)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(player.getUniqueId()))
        {
            final PlayerData playerData = new PlayerData(t.getPlayer().getMetaStore());;

            playerData.getActiveItems().put(groupId, itemId);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
