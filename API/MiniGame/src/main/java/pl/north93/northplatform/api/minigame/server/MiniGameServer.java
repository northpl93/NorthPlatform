package pl.north93.northplatform.api.minigame.server;

import static pl.north93.northplatform.api.global.network.server.ServerType.NORMAL;


import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.shared.impl.arena.ArenaManager;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.Server;

public class MiniGameServer extends Component
{
    @Inject
    private BukkitApiCore        bukkitApiCore;
    @Inject
    private IBukkitServerManager bukkitServer;
    @Inject
    private ArenaManager         arenaManager;
    private IServerManager       serverManager;

    @Override
    protected void enableComponent()
    {
        final Server server = this.bukkitServer.getServer();
        this.serverManager = (server.getType() == NORMAL) ? new LobbyManager() : new GameHostManager();
        this.serverManager.start();
    }

    @Override
    protected void disableComponent()
    {
        this.serverManager.stop();
    }

    public ArenaManager getArenaManager()
    {
        return this.arenaManager;
    }

    @SuppressWarnings("unchecked")
    public <T extends IServerManager> T getServerManager()
    {
        //noinspection unchecked
        return (T) this.serverManager;
    }
}
