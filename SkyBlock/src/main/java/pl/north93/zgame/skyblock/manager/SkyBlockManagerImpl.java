package pl.north93.zgame.skyblock.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import pl.north93.zgame.skyblock.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;

public class SkyBlockManagerImpl implements ISkyBlockManager
{
    private Logger logger;
    private final SkyBlockManager skyBlockManager;
    private final List<UUID>      connectedServers;

    public SkyBlockManagerImpl(final SkyBlockManager skyBlockManager)
    {
        this.skyBlockManager = skyBlockManager;
        this.connectedServers = new ArrayList<>();
    }

    @Override
    public ServerMode serverJoin(final UUID serverId)
    {
        final List<UUID> servers = this.skyBlockManager.getSkyBlockConfig().getSkyBlockServers();
        if (servers.contains(serverId))
        {
            this.connectedServers.add(serverId);
            this.logger.info("[SkyBlock] Island host " + serverId + " connected to network.");
            return ServerMode.ISLAND_HOST;
        }
        return ServerMode.LOBBY;
    }

    @Override
    public void serverDisconnect(final UUID serverId)
    {
        if (this.connectedServers.remove(serverId))
        {
            this.logger.info("[SkyBlock] Island host " + serverId + " disconnected.");
        }
    }

    @Override
    public SkyBlockConfig getConfig()
    {
        return this.skyBlockManager.getSkyBlockConfig();
    }

    @Override
    public void createIsland(final UUID ownerUuid)
    {

    }
}
