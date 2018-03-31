package pl.arieals.api.minigame.server;

import static pl.north93.zgame.api.global.network.server.ServerType.NORMAL;


import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.server.IBukkitServerManager;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.Server;

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
