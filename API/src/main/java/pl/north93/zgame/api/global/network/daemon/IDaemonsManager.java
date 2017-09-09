package pl.north93.zgame.api.global.network.daemon;

import java.util.Set;

import pl.north93.zgame.api.global.redis.observable.Hash;

public interface IDaemonsManager
{
    /**
     * Zwraca aktualną listę demonów podłączonych do sieci.
     *
     * @return lista demonów.
     */
    Set<DaemonDto> all();

    DaemonRpc getRpc(String daemonId);

    default DaemonRpc getRpc(final DaemonDto daemonDto)
    {
        return this.getRpc(daemonDto.getName());
    }

    Unsafe unsafe();

    interface Unsafe
    {
        Hash<DaemonDto> getHash();
    }
}
