package pl.north93.northplatform.api.minigame.server.gamehost.deathmatch;

public interface IFightManager
{
    boolean isFightStarted();

    int getTimeToStart();

    void cancel();
}
