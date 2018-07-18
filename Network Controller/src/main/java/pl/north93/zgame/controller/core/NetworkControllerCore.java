package pl.north93.zgame.controller.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.permissions.GroupsContainer;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.controller.configserver.IConfigServer;
import pl.north93.zgame.controller.configserver.source.XmlConfigSource;

public class NetworkControllerCore extends Component
{
    private final Logger logger = LoggerFactory.getLogger(NetworkControllerCore.class);
    @Inject
    private IRpcManager   rpcManager;
    @Inject
    private IConfigServer configServer;

    @Override
    protected void enableComponent()
    {
        this.logger.info("Starting NetworkController...");
        if (this.getApiCore().getPlatform() == Platform.BUNGEE) // on standalone platform context will be added automatically from getId()
        {
            this.rpcManager.addListeningContext("controller");
        }
        this.rpcManager.addRpcImplementation(NetworkControllerRpc.class, new NetworkControllerRpcImpl());

        // rejestrujemy glowny config sieci.
        this.configServer.addConfig("networkMeta", new XmlConfigSource<>(NetworkMeta.class, this.getApiCore().getFile("network.xml")));

        // rejestrujemy config z uprawnieniami
        this.configServer.addConfig("groups", new XmlConfigSource<>(GroupsContainer.class, this.getApiCore().getFile("permissions.xml")));
    }

    @Override
    protected void disableComponent()
    {
        this.logger.info("Network Controller stopped!");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
