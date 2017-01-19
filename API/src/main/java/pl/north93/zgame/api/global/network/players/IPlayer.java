package pl.north93.zgame.api.global.network.players;

import java.util.UUID;

import pl.north93.zgame.api.global.metadata.Metadatable;
import pl.north93.zgame.api.global.permissions.Group;

public interface IPlayer extends Metadatable
{
    UUID getUuid();

    /**
     * Zwraca nick pod którym był ostatnio widziany gracz.
     * @return ostatnio widziany nick.
     */
    String getLatestNick();

    Group getGroup();

    void setGroup(Group group);

    /**
     * Sprawdza czy ten gracz jest online na serwerze.
     *
     * @return true - jesli gracz jest online.
     */
    boolean isOnline();
}
