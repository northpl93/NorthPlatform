package pl.north93.zgame.controller.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.network.proxy.AntiDdosConfig;
import pl.north93.zgame.api.global.permissions.GroupsContainer;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.controller.configserver.IConfigServer;
import pl.north93.zgame.controller.configserver.source.XmlConfigSource;

@Slf4j
public class NetworkControllerCore extends Component
{
    @Inject
    private IRpcManager   rpcManager;
    @Inject
    private IConfigServer configServer;

    @Override
    protected void enableComponent()
    {
        log.info("Starting NetworkController...");
        this.rpcManager.addRpcImplementation(NetworkControllerRpc.class, new NetworkControllerRpcImpl());

        // rejestrujemy glowny config sieci.
        this.configServer.addConfig("networkMeta", new XmlConfigSource<>(NetworkMeta.class, this.getApiCore().getFile("network.xml")));

        // rejestrujemy config z uprawnieniami
        this.configServer.addConfig("groups", new XmlConfigSource<>(GroupsContainer.class, this.getApiCore().getFile("permissions.xml")));

        // rejestrujemy config z konfiguracja Anty DDoS dla serwerów proxy
        this.configServer.addConfig("antiddos", new XmlConfigSource<>(AntiDdosConfig.class, this.getApiCore().getFile("antiddos.xml")));
    }

    @Override
    protected void disableComponent()
    {
        log.info("Network Controller stopped!");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
