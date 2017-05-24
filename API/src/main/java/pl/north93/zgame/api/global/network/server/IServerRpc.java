package pl.north93.zgame.api.global.network.server;

public interface IServerRpc
{
    Integer getOnlinePlayers();

    Boolean isShutdownScheduled();

    void setShutdownScheduled();
}
