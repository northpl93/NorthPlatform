package pl.north93.northplatform.api.global.network.players;

public interface IOfflinePlayer extends IPlayer
{
    IOnlinePlayer asOnlinePlayer();

    @Override
    default Identity getIdentity()
    {
        return Identity.create(this.getUuid(), null);
    }
}
