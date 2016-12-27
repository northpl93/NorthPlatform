package pl.north93.zgame.skyblock.api;

import java.util.UUID;

import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;

public interface ISkyBlockManager
{
    ServerMode serverJoin(UUID serverId);

    void serverDisconnect(UUID serverId);

    SkyBlockConfig getConfig();

    // Ta metoda powinna sama znaleźć odpowiedni serwer
    // i przekazać mu info że trzeba zrobić wyspę
    void createIsland(UUID ownerUuid);
}
