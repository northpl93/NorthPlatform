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
import pl.north93.zgame.skyblock.server.listeners.SetupListeners;
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
        SetupListeners.setup(this);
        this.getApiCore().getLogger().info("[SkyBlock] SkyBlock started...");
    }

    @Override
    protected void disableComponent()
    {
        this.serverManager.stop();
        this.skyBlockManager.serverDisconnect(this.bukkitApiCore.getServer().get().getUuid());
    }

    /**
     * Zwraca DAO służące do obsługi danych wysp.
     * @see pl.north93.zgame.skyblock.api.IslandData
     * @return Data Access Object dla IslandData
     */
    public IslandDao getIslandDao()
    {
        return this.islandDao;
    }

    /**
     * Zwraca tryb w jakim pracuje ten serwer.
     * @return tryb w którym pracuje ten serwer.
     */
    public ServerMode getServerMode()
    {
        return this.serverMode;
    }

    /**
     * Zwraca klasę proxy służącą do komunikacji z kontrolerem sieci.
     * @return proxy do komunikacji z kontrolerem sieci.
     */
    public ISkyBlockManager getSkyBlockManager()
    {
        return this.skyBlockManager;
    }

    public SkyBlockConfig getSkyBlockConfig()
    {
        return this.skyBlockConfig;
    }

    /**
     * Zwraca klasę zarządzającą tym typem serwera SkyBlocka
     * @param <T> IslandHostManager lub LobbyManager
     * @return menadżer tego typu serwera.
     */
    public <T extends ISkyBlockServerManager> T getServerManager()
    {
        //noinspection unchecked
        return (T) this.serverManager;
    }
}
