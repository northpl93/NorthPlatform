package pl.arieals.api.minigame.server.gamehost.deathmatch;

public interface IFightManager
{
    boolean isFightStarted();

    int getTimeToStart();

    void cancel();
}
