package pl.north93.zgame.controller.servers.groups;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.controller.servers.cfg.AutoScalingConfig;
import pl.north93.zgame.controller.servers.cfg.ServersGroupConfig;

public class LocalGroupsManager
{
    @Inject
    private Logger                          logger;
    @Inject
    private INetworkManager                 networkManager;
    @Inject @NetConfig(type = AutoScalingConfig.class, id = "autoscaler")
    private IConfig<AutoScalingConfig>      config;
    private Map<String, ILocalServersGroup> localGroups = new HashMap<>();

    @Bean
    private LocalGroupsManager()
    {
    }

    public void loadGroups()
    {
        final AutoScalingConfig config = this.config.get();

        for (final ServersGroupConfig groupConfig : config.getServersGroups())
        {
            this.createGroupFromConfig(groupConfig);
        }
    }

    private void createGroupFromConfig(final ServersGroupConfig config)
    {
        final Hash<ServersGroupDto> serversGroups = this.networkManager.getServers().unsafe().getServersGroups();

        final ILocalServersGroup localGroup = config.createLocalGroup();
        this.localGroups.put(localGroup.getName(), localGroup);
        serversGroups.put(localGroup.getName(), localGroup.getAsDto());

        localGroup.init();

        this.logger.log(Level.INFO, "Created {0} group with name {1}", new Object[]{config.getType(), config.getName()});
    }
}
