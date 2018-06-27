package pl.arieals.api.minigame.shared.impl.arena;

import pl.arieals.api.minigame.shared.api.arena.reconnect.IReconnectManager;
import pl.arieals.api.minigame.shared.api.arena.reconnect.ReconnectTicket;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

public class ReconnectManagerImpl implements IReconnectManager
{
    @Inject
    private ApiCore         apiCore;
    @Inject
    private INetworkManager networkManager;

    @Bean
    private ReconnectManagerImpl()
    {
    }

    @Override
    public ReconnectTicket getReconnectTicket(final Identity identity)
    {
        final IPlayer player = this.networkManager.getPlayers().unsafe().getNullable(identity);
        if (player == null)
        {
            return null;
        }

        final MetaStore metaStore = player.getMetaStore();
        if (ReconnectTicket.hasTicket(metaStore))
        {
            return new ReconnectTicket(metaStore);
        }

        return null;
    }

    @Override
    public void updateReconnectTicket(final Identity player, final ReconnectTicket newTicket)
    {
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(player))
            {
                final MetaStore metaStore = t.getPlayer().getMetaStore();
                newTicket.setTicket(metaStore);
            }
        });
    }
}
