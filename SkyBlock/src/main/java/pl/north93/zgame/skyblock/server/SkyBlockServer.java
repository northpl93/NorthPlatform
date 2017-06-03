package pl.north93.zgame.skyblock.server;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.skyblock.shared.api.IIslandsRanking;
import pl.north93.zgame.skyblock.shared.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.shared.impl.IslandDao;
import pl.north93.zgame.skyblock.shared.impl.IslandsRankingImpl;
import pl.north93.zgame.skyblock.shared.api.ServerMode;
import pl.north93.zgame.skyblock.shared.api.cfg.SkyBlockConfig;
import pl.north93.zgame.skyblock.server.listeners.SetupListeners;
import pl.north93.zgame.skyblock.server.management.ISkyBlockServerManager;
import pl.north93.zgame.skyblock.server.management.ServerManagerFactory;
import pl.north93.zgame.skyblock.server.world.Island;

@IncludeInScanning("pl.north93.zgame.skyblock.shared")
public class SkyBlockServer extends Component
{
    private BukkitApiCore          bukkitApiCore;
    @Inject
    private IRpcManager            rpcManager;
    private IslandDao              islandDao;
    private ServerMode             serverMode;
    private SkyBlockConfig         skyBlockConfig;
    private ISkyBlockManager       skyBlockManager;
    private ISkyBlockServerManager serverManager;
    private IIslandsRanking        IIslandsRanking;

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
        this.IIslandsRanking = new IslandsRankingImpl();
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
     * @see pl.north93.zgame.skyblock.shared.api.IslandData
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

    /**
     * Zwraca klasę pomocniczą obsługującą ranking skyblocka.
     * @return klasa do obsługi rankingu.
     */
    public IIslandsRanking getIslandsRanking()
    {
        return this.IIslandsRanking;
    }

    public boolean canAccess(final Player player, final Island island)
    {
        return island != null && island.canBuild(player.getUniqueId()) || player.hasMetadata("skyblockbypass");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
