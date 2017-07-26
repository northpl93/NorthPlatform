package pl.north93.zgame.controller.configbroadcaster;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.deployment.serversgroup.ServersGroupsContainer;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.permissions.GroupsContainer;
import pl.north93.zgame.controller.cfg.ServersGroupsConfig;
import pl.north93.zgame.controller.cfg.ServersPatternsConfig;
import pl.north93.zgame.controller.configserver.IConfigServer;
import pl.north93.zgame.controller.configserver.source.IConfigSource;
import pl.north93.zgame.controller.configserver.source.TransformedConfigSource;
import pl.north93.zgame.controller.configserver.source.YamlConfigSource;

/**
 * Komponent legacy rejestrujacy podstawowe configi API w
 * systemie konfiguracji.
 */
public class ConfigBroadcaster extends Component
{
    @Inject
    private IConfigServer                   configServer;
    private IConfig<ServersGroupsContainer> serversGroups;

    @Override
    protected void enableComponent()
    {
        this.loadAndBroadcastConfigs();
    }

    @Override
    protected void disableComponent()
    {
    }

    public ServersGroupsContainer getServersGroups()
    {
        return this.serversGroups.get();
    }

    public void loadAndBroadcastConfigs()
    {
        final ApiCore api = this.getApiCore();

        final IConfigSource<ServersGroupsConfig> untransformedGroups = new YamlConfigSource<>(ServersGroupsConfig.class, api.getFile("serversgroups.yml"));
        final IConfigSource<ServersGroupsContainer> transformedGroups = new TransformedConfigSource<>(untransformedGroups, ServersGroupsContainer.class, ServersGroupsConfig::toContainer);
        this.serversGroups = this.configServer.addConfig("serversGroups", transformedGroups);

        this.configServer.addConfig("networkMeta", new YamlConfigSource<>(NetworkMeta.class, api.getFile("network.yml")));
        this.configServer.addConfig("groups", new YamlConfigSource<>(GroupsContainer.class, api.getFile("permissions.yml")));
        this.configServer.addConfig("patternsConfig", new YamlConfigSource<>(ServersPatternsConfig.class, api.getFile("instancepatterns.yml")));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serversGroups", this.serversGroups).toString();
    }
}
