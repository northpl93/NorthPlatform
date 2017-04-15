package pl.arieals.api.minigame.server;

import static pl.north93.zgame.api.global.network.server.ServerType.NORMAL;


import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.api.global.network.server.Server;

@IncludeInScanning("pl.arieals.api.minigame.shared")
public class MiniGameServer extends Component
{
    private BukkitApiCore  bukkitApiCore;
    private ArenaManager   arenaManager;
    private IServerManager serverManager;

    @Override
    protected void enableComponent()
    {
        this.arenaManager = new ArenaManager();

        final Server server = this.bukkitApiCore.getServer().get();
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

    public <T extends IServerManager> T getServerManager()
    {
        //noinspection unchecked
        return (T) this.serverManager;
    }
}
