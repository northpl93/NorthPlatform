package pl.north93.zgame.api.bungee.mods.impl;

import pl.north93.zgame.api.bungee.mods.IBungeeServersManager;
import pl.north93.zgame.api.global.network.server.ServerProxyData;

/**
 * Klasa zarządzająca serwerami dostępnymi w Proxy.
 * Odpowiada za modyfikacje refleksjami tej listy.
 */
public class BungeeServersManager implements IBungeeServersManager
{
    //private final Class<?> bungeeConfiguration = getCanonicalClass("net.md_5.bungee.conf.Configuration");

    @Override
    public void addServer(final ServerProxyData proxyData)
    {

    }

    @Override
    public void removeServer(final String serverName)
    {

    }

    @Override
    public void removeAllServers()
    {

    }
}
