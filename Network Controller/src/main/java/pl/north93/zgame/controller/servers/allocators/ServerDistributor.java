package pl.north93.zgame.controller.servers.allocators;

import java.util.Set;

import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.network.server.Server;

/**
 * Klasa odpowiedzialna za szukanie najlepszych demonów do uruchomienia serwerów.
 */
public class ServerDistributor
{
    public RemoteDaemon findBestDaemonFor(final Set<RemoteDaemon> daemons, final Server server)
    {
        final int requiredRam = server.getServerPattern().getMaxMemory();

        RemoteDaemon bestDaemon = null;
        for (final RemoteDaemon daemon : daemons)
        {
            if (! daemon.isAcceptingServers() || requiredRam > this.daemonFreeRam(daemon))
            {
                continue;
            }

            if (bestDaemon == null)
            {
                bestDaemon = daemon;
                continue;
            }

            if (daemon.getRamUsed() < bestDaemon.getRamUsed())
            {
                bestDaemon = daemon;
            }
        }

        return bestDaemon;
    }

    private int daemonFreeRam(final RemoteDaemon daemon)
    {
        return daemon.getMaxRam() - daemon.getRamUsed();
    }
}
