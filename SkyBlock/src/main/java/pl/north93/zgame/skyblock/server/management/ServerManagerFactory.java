package pl.north93.zgame.skyblock.server.management;

import pl.north93.zgame.skyblock.api.ServerMode;

public class ServerManagerFactory
{
    public static final ServerManagerFactory INSTANCE = new ServerManagerFactory();

    public ISkyBlockServerManager getServerManager(final ServerMode serverMode)
    {
        switch (serverMode)
        {
            case ISLAND_HOST:
                return new IslandHostManager();
            case LOBBY:
                return new LobbyManager();
        }
        throw new IllegalArgumentException();
    }
}
