package pl.north93.zgame.api.global.network.daemon;

import java.util.Set;

public interface IDaemonsManager
{
    /**
     * Zwraca aktualną listę demonów podłączonych do sieci.
     *
     * @return lista demonów.
     */
    Set<DaemonDto> getDaemons();
}
