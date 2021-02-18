package pl.north93.northplatform.api.global.network.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.api.global.config.NetConfig;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.NetworkControllerRpc;
import pl.north93.northplatform.api.global.network.NetworkMeta;
import pl.north93.northplatform.api.global.network.event.NetworkShutdownNetEvent;
import pl.north93.northplatform.api.global.network.mojang.IMojangCache;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.redis.rpc.Targets;

@Slf4j
class NetworkManager extends Component implements INetworkManager
{
    @Inject @NetConfig(type = NetworkMeta.class, id = "networkMeta")
    private IConfig<NetworkMeta> networkConfig;
    @Inject
    private IRpcManager rpcManager;
    @Inject
    private IMojangCache mojangCache;

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public IConfig<NetworkMeta> getNetworkConfig()
    {
        return this.networkConfig;
    }

    /**
     * Zwraca instancję zdalnego wywoływania procedur do kontrolera sieci.
     *
     * @return NetworkControllerRpc
     */
    @Override
    public NetworkControllerRpc getNetworkController()
    {
        return this.rpcManager.createRpcProxy(NetworkControllerRpc.class, Targets.networkController());
    }

    @Override
    public IMojangCache getMojang()
    {
        return this.mojangCache;
    }

    @NetEventSubscriber(NetworkShutdownNetEvent.class)
    public void onNetShutdownEvent(final NetworkShutdownNetEvent event)
    {
        log.info("Received network stop event");
        this.getApiCore().getHostConnector().shutdownHost();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
