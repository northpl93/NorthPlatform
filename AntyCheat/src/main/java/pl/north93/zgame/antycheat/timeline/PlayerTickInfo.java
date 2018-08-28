package pl.north93.zgame.antycheat.timeline;

import org.bukkit.entity.Player;

public interface PlayerTickInfo
{
    Player getOwner();

    Tick getTick();

    int getPing();

    boolean isShortAfterSpawn();

    boolean isShortAfterTeleport();

    /**
     * @return True jesli otrzymano pakiet od gracza w tym ticku.
     */
    boolean hasReceivedPacket();

    boolean isGliding();

    double getMovementSpeed();
}
