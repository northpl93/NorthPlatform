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

    // Ta metoda powinna sama znaleźć odpowiedni serwer
    // i przekazać mu info że trzeba zrobić wyspę
    @DoNotWaitForResponse
    void createIsland(String islandType, String ownerNick);

    // metoda przekazuje odpowiedniemu serwerowi info, ze trzeba
    // usunac wyspe, usuwa ja z bazy danych i z metadanych gracza
    @DoNotWaitForResponse
    void deleteIsland(UUID islandId);

    // zmienia biom na wyspie
    @DoNotWaitForResponse
    void changeBiome(UUID islandId, NorthBiome biome);

    // dodajemy gracza do listy zaproszonych i wysyłamy mu informacje
    // ofc sprawdzamy czy możemy to wszystko zrobić
    @DoNotWaitForResponse
    void invitePlayer(UUID islandId, String invitedPlayer);

    // gracz wysyła info, że chce zaakceptować zaproszenie
    @DoNotWaitForResponse
    void invitationAccepted(UUID islandId, String invitedPlayer);

    // wyrzuca danego gracza z listy osób uprawnionych do tej wyspy
    @DoNotWaitForResponse
    void leaveIsland(UUID islandId, String invoker, String leavingPlayer, Boolean isSelfLeaving);

    @DoNotWaitForResponse
    void visitIsland(UUID islandId, String visitor);

    void setShowInRanking(UUID islandId, Boolean show); // will wait for response
}
