package pl.north93.zgame.datashare.netcontroller;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareController;
import pl.north93.zgame.datashare.api.cfg.DataSharingConfig;
import pl.north93.zgame.datashare.api.cfg.DataSharingGroupConfig;

public class PlayerDataShareController extends Component implements IDataShareController
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager         rpcManager;
    private DataSharingConfig   config;

    @Override
    protected void enableComponent()
    {
        this.config = ConfigUtils.loadConfigFile(DataSharingConfig.class, this.getApiCore().getFile("datasharing.yml"));
        this.rpcManager.addRpcImplementation(IDataShareController.class, this);
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public DataSharingGroup getMyGroup(final UUID serverId)
    {
        final Server server = this.networkManager.getServer(serverId).get();

        for (final DataSharingGroupConfig dataSharingGroupConfig : this.config.getSharingGroups())
        {
            final Optional<IServersGroup> serverGroup = server.getServersGroup();
            if (serverGroup.isPresent())
            {
                if (dataSharingGroupConfig.getServersGroups().contains(serverGroup.get().getName()))
                {
                    return new DataSharingGroup(dataSharingGroupConfig);
                }
            }

            if (dataSharingGroupConfig.getServers().contains(server.getUuid().toString()))
            {
                return new DataSharingGroup(dataSharingGroupConfig);
            }
        }

        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("config", this.config).toString();
    }
}
