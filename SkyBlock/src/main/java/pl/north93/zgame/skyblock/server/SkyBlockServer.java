package pl.north93.zgame.skyblock.server;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.skyblock.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.api.IslandDao;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;
import pl.north93.zgame.skyblock.server.management.ISkyBlockServerManager;
import pl.north93.zgame.skyblock.server.management.ServerManagerFactory;

public class SkyBlockServer extends Component
{
    private BukkitApiCore          bukkitApiCore;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager            rpcManager;
    private IslandDao              islandDao;
    private ServerMode             serverMode;
    private SkyBlockConfig         skyBlockConfig;
    private ISkyBlockManager       skyBlockManager;
    private ISkyBlockServerManager serverManager;

    @Override
    protected void enableComponent()
    {
        this.skyBlockManager = this.rpcManager.createRpcProxy(ISkyBlockManager.class, Targets.networkController());
        this.skyBlockConfig = this.skyBlockManager.getConfig();
        this.islandDao = new IslandDao();
        this.serverMode = this.skyBlockManager.serverJoin(this.bukkitApiCore.getServer().get().getUuid());
        this.getApiCore().getLogger().info("[SkyBlock] Server is running in " + this.serverMode + " mode");
        this.serverManager = ServerManagerFactory.INSTANCE.getServerManager(this.serverMode);
        this.serverManager.start();
        this.getApiCore().getLogger().info("[SkyBlock] SkyBlock started...");
    }

    @Override
    protected void disableComponent()
    {
        this.serverManager.stop();
        this.skyBlockManager.serverDisconnect(this.bukkitApiCore.getServer().get().getUuid());
    }

    public IslandDao getIslandDao()
    {
        return this.islandDao;
    }

    public ServerMode getServerMode()
    {
        return this.serverMode;
    }

    public ISkyBlockManager getSkyBlockManager()
    {
        return this.skyBlockManager;
    }

    public SkyBlockConfig getSkyBlockConfig()
    {
        return this.skyBlockConfig;
    }
}
