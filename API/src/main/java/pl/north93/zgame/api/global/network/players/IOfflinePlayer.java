package pl.north93.zgame.api.global.network.players;

public interface IOfflinePlayer extends IPlayer
{
    IOnlinePlayer asOnlinePlayer();

    @Override
    default Identity getIdentity()
    {
        return Identity.create(this.getUuid(), null);
    }
}
