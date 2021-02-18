package pl.north93.northplatform.api.minigame.shared.impl.arena;

import pl.north93.northplatform.api.global.HostConnector;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.shared.api.arena.reconnect.IReconnectManager;
import pl.north93.northplatform.api.minigame.shared.api.arena.reconnect.ReconnectTicket;

public class ReconnectManagerImpl implements IReconnectManager
{
    @Inject
    private HostConnector hostConnector;
    @Inject
    private IPlayersManager playersManager;

    @Bean
    private ReconnectManagerImpl()
    {
    }

    @Override
    public ReconnectTicket getReconnectTicket(final Identity identity)
    {
        final IPlayer player = this.playersManager.unsafe().getNullable(identity);
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
        this.hostConnector.runTaskAsynchronously(() ->
        {
            try (final IPlayerTransaction t = this.playersManager.transaction(player))
            {
                final MetaStore metaStore = t.getPlayer().getMetaStore();
                newTicket.setTicket(metaStore);
            }
        });
    }
}
