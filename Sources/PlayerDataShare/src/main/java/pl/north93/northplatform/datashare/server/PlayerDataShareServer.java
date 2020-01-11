package pl.north93.northplatform.datashare.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.redis.rpc.Targets;
import pl.north93.northplatform.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.northplatform.datashare.api.DataSharingGroup;
import pl.north93.northplatform.datashare.api.IDataShareController;
import pl.north93.northplatform.datashare.server.listeners.ChatSharingManager;
import pl.north93.northplatform.datashare.server.listeners.PlayerDataIntegrationListener;
import pl.north93.northplatform.datashare.server.listeners.PlayerDataListener;
import pl.north93.northplatform.datashare.server.listeners.PlayerJoinListener;
import pl.north93.northplatform.datashare.server.listeners.PlayerLeftListener;

@Slf4j
public class PlayerDataShareServer extends Component
{
    private static final int                DATA_PERSIST_TASK = 20 * 60 * 5;
    @Inject
    private              BukkitApiCore      apiCore;
    @Inject
    private              IRpcManager        rpcManager;
    @Inject
    private              RedisSubscriber    redisSubscriber;
    private              ChatSharingManager chatSharingManager;
    private              DataSharingGroup   sharingGroup;

    @Override
    protected void enableComponent()
    {
        final IDataShareController controller = this.rpcManager.createRpcProxy(IDataShareController.class, Targets.networkController());
        this.sharingGroup = controller.getMyGroup(this.apiCore.getServerId());
        if (this.sharingGroup == null)
        {
            log.warn("PlayerDataShare plugin is enabled, but sharing group is unknown.");
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