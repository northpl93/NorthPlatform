package pl.north93.zgame.api.global.network.players;

public interface IPlayerTransaction extends AutoCloseable
{
    /**
     * Checks if player in this transaction is online.
     * @return true if player is online.
     */
    boolean isOnline();

    IPlayer getPlayer();
}
