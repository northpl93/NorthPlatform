package pl.north93.zgame.antycheat.timeline;

import org.bukkit.entity.Player;

public interface PlayerTickInfo
{
    Player getOwner();

    Tick getTick();

    boolean isShortAfterSpawn();

    /**
     * @return True jesli otrzymano pakiet od gracza w tym ticku.
     */
    boolean hasReceivedPacket();

    /**
     * @return Ping gracza w danym ticku.
     */
    int getPing();
}
