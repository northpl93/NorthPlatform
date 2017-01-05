package pl.north93.zgame.skyblock.api;

import java.util.UUID;

import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;
import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;

public interface ISkyBlockManager
{
    ServerMode serverJoin(UUID serverId);

    @DoNotWaitForResponse
    void serverDisconnect(UUID serverId);

    SkyBlockConfig getConfig();

    @DoNotWaitForResponse
    void teleportToIsland(String playerName, UUID islandId);

    @DoNotWaitForResponse
    void updateIslandData(IslandData islandData);

    // Ta metoda powinna sama znaleźć odpowiedni serwer
    // i przekazać mu info że trzeba zrobić wyspę
    @DoNotWaitForResponse
    void createIsland(String islandType, String ownerNick);

    // metoda przekazuje odpowiedniemu serwerowi info, ze trzeba
    // usunac wyspe, usuwa ja z bazy danych i z metadanych gracza
    @DoNotWaitForResponse
    void deleteIsland(UUID islandId);
}
