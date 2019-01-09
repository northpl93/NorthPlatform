package pl.north93.northplatform.api.bungee.proxy.impl;

import net.md_5.bungee.api.ProxyServer;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.proxy.ProxyDto;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.bungee.BungeeApiCore;

/*default*/ class ProxyInfoUploader
{
    private static final int             UPDATE_PROXY_DATA_EVERY = 20;
    @Inject
    private              BungeeApiCore   apiCore;
    @Inject
    private              INetworkManager networkManager;
    @Inject
    private              AntiDdosState   antiDdosState;

    @Bean
    private ProxyInfoUploader()
    {
        this.uploadInfo();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(this::uploadInfo, UPDATE_PROXY_DATA_EVERY);
    }

    private void uploadInfo()
    {
        final Hash<ProxyDto> hash = this.networkManager.getProxies().unsafe().getHash();
        hash.put(this.apiCore.getId(), this.generateInfo());
    }

    private ProxyDto generateInfo()
    {
        final ProxyDto proxyInstanceInfo = new ProxyDto();

        proxyInstanceInfo.setId(this.apiCore.getProxyConfig().getUniqueName());
        proxyInstanceInfo.setHostname(this.apiCore.getHostName());
        proxyInstanceInfo.setOnlinePlayers(ProxyServer.getInstance().getOnlineCount());
        proxyInstanceInfo.setAntiDdosState(this.antiDdosState.isEnabled());

        return proxyInstanceInfo;
    }
}
