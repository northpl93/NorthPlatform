package pl.north93.northplatform.datashare.netcontroller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.utils.ConfigUtils;
import pl.north93.northplatform.datashare.api.DataSharingGroup;
import pl.north93.northplatform.datashare.api.IDataShareController;
import pl.north93.northplatform.datashare.api.cfg.AnnouncerConfig;
import pl.north93.northplatform.datashare.api.cfg.DataSharingConfig;
import pl.north93.northplatform.datashare.api.cfg.DataSharingGroupConfig;

public class PlayerDataShareController extends Component implements IDataShareController
{
    @Inject
    private IServersManager serversManager;
    @Inject
    private IRpcManager rpcManager;
    private DataSharingConfig config;
    private final Map<UUID, DataSharingGroup> servers = new HashMap<>();

    @Override
    protected void enableComponent()
    {
        this.loadConfig();
        this.rpcManager.addRpcImplementation(IDataShareController.class, this);
        this.enableAnnouncers();
    }

    private void loadConfig()
    {
        final File configFile = this.getApiCore().getFile("datasharing.xml");
        this.config = ConfigUtils.loadConfig(DataSharingConfig.class, configFile);

        this.servers.clear();
        for (final DataSharingGroupConfig groupConfig : this.config.getSharingGroups())
        {
            final DataSharingGroup group = new DataSharingGroup(groupConfig);
            for (final String serverId : groupConfig.getServers())
            {
                this.servers.put(UUID.fromString(serverId), group);
            }
            for (final String serversGroup : groupConfig.getServersGroups())
            {
                for (final Server server : this.serversManager.inGroup(serversGroup))
                {
                    this.servers.put(server.getUuid(), group);
                }
            }
        }
    }

    private void enableAnnouncers()
    {
        for (final DataSharingGroupConfig config : this.config.getSharingGroups())
        {
            final AnnouncerConfig announcer = config.getAnnouncer();
            if (! announcer.isEnabled())
            {
                continue;
            }
            final BroadcastTask task = new BroadcastTask(new DataSharingGroup(config));
            this.getApiCore().getHostConnector().runTaskAsynchronously(task, announcer.getTime() * 20);
        }
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public DataSharingGroup getMyGroup(final UUID serverId)
    {
        return this.servers.get(serverId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("config", this.config).toString();
    }
}
