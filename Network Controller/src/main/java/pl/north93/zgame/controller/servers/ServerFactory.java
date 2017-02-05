package pl.north93.zgame.controller.servers;

import static pl.north93.zgame.api.global.network.server.ServerState.ALLOCATING;


import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.ManagedServersGroup;
import pl.north93.zgame.api.global.network.server.ServerImpl;

public class ServerFactory
{
    public static final ServerFactory INSTANCE = new ServerFactory();

    /**
     * Tworzy nową instancję serwera należącą do podanej grupy serwerów.
     * Serwer uznawany jest za uruchamiany przez demona.
     * UUID jest losowe.
     * Stan serwera ustalony jest na ALLOCATING (demon zmieni go gdy otrzyma serwer do instalacji)
     *
     * @param serversGroup grupa serwerów do której należeć ma serwer.
     * @return instancja serwera w stanie ALLOCATING. Nie jest zapisana w Redisie!
     */
    public ServerImpl createNewServer(final ManagedServersGroup serversGroup)
    {
        final ServerPattern serverPattern = API.getNetworkManager().getServerPattern(serversGroup.getServerPattern());
        return new ServerImpl(UUID.randomUUID(), true, serversGroup.getServersType(), ALLOCATING, serversGroup.getJoiningPolicy(), "", 0, serversGroup, serverPattern);
    }
}
