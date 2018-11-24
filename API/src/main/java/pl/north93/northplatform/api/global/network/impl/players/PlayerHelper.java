package pl.north93.northplatform.api.global.network.impl.players;

import lombok.ToString;
import pl.north93.northplatform.api.global.network.proxy.IProxyRpc;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;

@ToString(exclude = {"playersManager", "serversManager"})
/*default*/ final class PlayerHelper
{
    public static PlayerHelper INSTANCE;

    @Inject
    private PlayersManagerImpl playersManager;
    @Inject
    private IServersManager    serversManager;

    @Bean
    private PlayerHelper()
    {
        INSTANCE = this;
    }

    public IProxyRpc getProxyRpc(final OnlinePlayerImpl player)
    {
        return this.playersManager.getPlayerProxyRpc(player);
    }

    public void connectTo(final OnlinePlayerImpl player, final Server server, final IServerJoinAction... actions)
    {
        final IProxyRpc proxyRpc = this.playersManager.getPlayerProxyRpc(player);
        proxyRpc.connectPlayer(player.getNick(), server.getProxyName(), new JoinActionsContainer(server.getUuid(), actions));
    }

    public void connectTo(final OnlinePlayerImpl player, final String serversGroupName, final IServerJoinAction... actions)
    {
        final IProxyRpc proxyRpc = this.playersManager.getPlayerProxyRpc(player);
        final Server server = this.serversManager.getLeastLoadedServerInGroup(serversGroupName);
        proxyRpc.connectPlayer(player.getNick(), server.getProxyName(), new JoinActionsContainer(server.getUuid(), actions));
    }
}
