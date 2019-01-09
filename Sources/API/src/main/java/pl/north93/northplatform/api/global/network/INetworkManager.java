package pl.north93.northplatform.api.global.network;

import pl.north93.northplatform.api.global.network.daemon.IDaemonsManager;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.proxy.IProxiesManager;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.api.global.network.mojang.IMojangCache;

public interface INetworkManager
{
    /**
     * Zwraca obiekt konfiguracji sieci. Moze sie on dynamicznie aktualizowac.
     *
     * @return konfiguracja sieci.
     */
    IConfig<NetworkMeta> getNetworkConfig();

    /**
     * Zwraca interfejs zdalnego wywolywania procedur zbindowany do instancji
     * kontrolera sieci.
     *
     * @return Interfejs RPC do wywolywania metod na kontrolerze.
     */
    NetworkControllerRpc getNetworkController();

    /**
     * Zwraca interfejs sluzacy do zarzadzania cache API mojangu.
     *
     * @return Interfejs do zarzadzania cache API Mojang.
     */
    IMojangCache getMojang();

    /**
     * Zwraca interfejs sluzacy do zarzadzania instancjami proxy w sieci.
     *
     * @return Interfejs do zarzadzania instancjami proxy.
     */
    IProxiesManager getProxies();

    IDaemonsManager getDaemons();

    IServersManager getServers();

    IPlayersManager getPlayers();
}
