package pl.north93.zgame.api.global.network.players;

public interface IPlayerTransaction extends AutoCloseable
{
    <T extends IPlayer> T getPlayer();

    /**
     * Checks if player in this transaction is online.
     * @return true if player is online.
     */
    boolean isOnline();

    default boolean isOffline()
    {
        return ! this.isOnline();
    }

    @Override
    void close();
}
