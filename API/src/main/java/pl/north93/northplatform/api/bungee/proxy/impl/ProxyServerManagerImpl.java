package pl.north93.northplatform.api.bungee.proxy.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.northplatform.api.bungee.proxy.impl.listener.JoinPermissionsChecker;
import pl.north93.northplatform.api.bungee.proxy.impl.listener.PermissionsListener;
import pl.north93.northplatform.api.bungee.proxy.impl.listener.PingListener;
import pl.north93.northplatform.api.bungee.proxy.impl.listener.PlayerNetworkListener;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.event.NetworkKickAllNetEvent;
import pl.north93.northplatform.api.global.network.proxy.IProxyRpc;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.bungee.BungeeApiCore;
import pl.north93.northplatform.api.bungee.proxy.IProxyServerList;
import pl.north93.northplatform.api.bungee.proxy.IProxyServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.proxy.ProxyDto;

class ProxyServerManagerImpl extends Component implements IProxyServerManager
{
    @Inject
    private BungeeApiCore       apiCore;
    @Inject
    private IRpcManager         rpcManager;
    @Inject
    private INetworkManager     networkManager;
    private ProxyServerListImpl proxyServerList;

    @Override
    protected void enableComponent()
    {
        this.proxyServerList = new ProxyServerListImpl();
        this.proxyServerList.synchronizeServers();

        this.rpcManager.addRpcImplementation(IProxyRpc.class, new ProxyRpcImpl());

        // rejestrujemy nasze listenery
        this.apiCore.registerListeners(new PingListener(), new PlayerNetworkListener(), new PermissionsListener(), new JoinPermissionsChecker());
    }

    @Override
    protected void disableComponent()
    {
        final Hash<ProxyDto> hash = this.networkManager.getProxies().unsafe().getHash();
        hash.delete(this.getApiCore().getId());
    }

    @NetEventSubscriber(NetworkKickAllNetEvent.class)
    public void onNetKickAllEvent(final NetworkKickAllNetEvent event) // nasluchuje na event rozlaczenia wszystkich graczy
    {
        for (final ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers())
        {
            proxiedPlayer.disconnect();
        }
    }

    @Override
    public IProxyServerList getServerList()
    {
        return this.proxyServerList;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
