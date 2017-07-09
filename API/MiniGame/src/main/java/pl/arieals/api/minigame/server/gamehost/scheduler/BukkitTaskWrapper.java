package pl.arieals.api.minigame.server.gamehost.scheduler;

interface BukkitTaskWrapper extends Runnable
{
    long getScheduledAt();

    boolean isRepeated();

    boolean isCancelled();

    long getPeriod();

    long getNextRun();

    void setNextRun(long nextRun);

    void cancel();
}
