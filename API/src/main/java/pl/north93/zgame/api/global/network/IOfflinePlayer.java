package pl.north93.zgame.api.global.network;

public interface IOfflinePlayer extends IPlayer
{
    IOnlinePlayer asOnlinePlayer();
}