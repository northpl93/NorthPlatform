package pl.north93.northplatform.api.minigame.server;

import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.DefinedProfile;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.ServerType;

class GameHostProfile extends DefinedProfile
{
    @Inject
    private IBukkitServerManager bukkitServer;

    public GameHostProfile()
    {
        super("gamehost");
    }

    @Override
    public boolean isEnabled()
    {
        final Server server = this.bukkitServer.getServer();
        return server.getType() == ServerType.MINIGAME;
    }
}

class LobbyProfile extends DefinedProfile
{
    @Inject
    private IBukkitServerManager bukkitServer;

    public LobbyProfile()
    {
        super("lobby");
    }

    @Override
    public boolean isEnabled()
    {
        final Server server = this.bukkitServer.getServer();
        return server.getType() == ServerType.NORMAL;
    }
}
