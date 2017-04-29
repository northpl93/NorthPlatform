package pl.north93.zgame.datashare.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareController;
import pl.north93.zgame.datashare.server.listeners.ChatSharingManager;
import pl.north93.zgame.datashare.server.listeners.PlayerDataIntegrationListener;
import pl.north93.zgame.datashare.server.listeners.PlayerDataListener;
import pl.north93.zgame.datashare.server.listeners.PlayerJoinListener;
import pl.north93.zgame.datashare.server.listeners.PlayerLeftListener;

public class PlayerDataShareServer extends Component
{
    private static final int DATA_PERSIST_TASK = 20 * 60 * 5;
    private BukkitApiCore      apiCore;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager        rpcManager;
    @InjectComponent("API.Database.Redis.Subscriber")
    private RedisSubscriber    redisSubscriber;
    private ChatSharingManager chatSharingManager;
    private DataSharingGroup   sharingGroup;

    @Override
    protected void enableComponent()
    {
        final IDataShareController controller = this.rpcManager.createRpcProxy(IDataShareController.class, Targets.networkController());
        this.sharingGroup = controller.getMyGroup(this.apiCore.getServer().get().getUuid());
        if (this.sharingGroup == null)
        {
            this.apiCore.getLogger().warning("PlayerDataShare plugin is enabled, but sharing group is unknown.");
            return;
        }

        this.chatSharingManager = new ChatSharingManager();
        this.chatSharingManager.start(this.sharingGroup);
        this.apiCore.registerEvents(new PlayerJoinListener(), new PlayerLeftListener(), new PlayerDataIntegrationListener());
        this.redisSubscriber.subscribe("playersdata:" + this.sharingGroup.getName(), new PlayerDataListener());
        this.apiCore.getPlatformConnector().runTaskAsynchronously(new InventoryPersistTask(), DATA_PERSIST_TASK);
    }

    @Override
    protected void disableComponent()
    {
        if (this.chatSharingManager != null) // may be null when sharingGroup is null
        {
            this.chatSharingManager.stop();
        }
    }

    public DataSharingGroup getMyGroup()
    {
        return this.sharingGroup;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("sharingGroup", this.sharingGroup).toString();
    }
}
