package pl.north93.northplatform.api.bungee.proxy.impl;

import java.util.Objects;

import lombok.ToString;
import net.md_5.bungee.api.ProxyServer;
import pl.north93.northplatform.api.bungee.BungeeApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.network.proxy.IProxiesManager;
import pl.north93.northplatform.api.global.network.proxy.ProxyDto;

@ToString(of = {"latestDto"})
/*default*/ class ProxyInfoUploader
{
    private static final int UPDATE_PROXY_DATA_EVERY = 20;
    private final BungeeApiCore apiCore;
    private final IProxiesManager proxiesManager;
    private final AntiDdosState antiDdosState;
    private ProxyDto latestDto;

    @Bean
    private ProxyInfoUploader(final BungeeApiCore apiCore, final IProxiesManager proxiesManager, final AntiDdosState antiDdosState)
    {
        this.apiCore = apiCore;
        this.proxiesManager = proxiesManager;
        this.antiDdosState = antiDdosState;

        this.uploadInfo();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(this::uploadInfo, UPDATE_PROXY_DATA_EVERY);
    }

    private void uploadInfo()
    {
        final ProxyDto newProxyInfo = this.generateInfo();
        if (Objects.equals(this.latestDto, newProxyInfo))
        {
            return;
        }

        this.latestDto = newProxyInfo;
        this.proxiesManager.addOrUpdateProxy(this.apiCore.getId(), newProxyInfo);
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
