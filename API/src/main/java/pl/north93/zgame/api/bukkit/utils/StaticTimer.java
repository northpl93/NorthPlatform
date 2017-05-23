package pl.north93.zgame.api.bukkit.utils;

import java.util.concurrent.TimeUnit;

public class StaticTimer
{
    private static final int ONE_TICK_MILIS = 50;
    private long    startTime;
    private long    baseTime;
    private boolean isGrowing;
    private long    stoppedTime;

    public StaticTimer()
    {
        this.stoppedTime = -1; // mark timer as stopped
    }

    /**
     * Uruchamia licznik rosnący/malejący z podanym czasem startowym.
     * @param baseTime czas startowy.
     * @param timeUnit jednostka czasu startowego.
     * @param growing czy ma rosnąć.
     */
    public void start(final long baseTime, final TimeUnit timeUnit, final boolean growing)
    {
        this.startTime = System.currentTimeMillis();
        this.baseTime = timeUnit.toMillis(baseTime);
        this.isGrowing = growing;
        this.stoppedTime = 0;
    }

    public void stop()
    {
        this.stoppedTime = this.getCurrentTime(TimeUnit.MILLISECONDS);
    }

    public boolean isStarted()
    {
        return this.stoppedTime == 0;
    }

    private long getTimeSpentAfterStart()
    {
        return System.currentTimeMillis() - this.startTime;
    }

    /**
     * Zwraca aktualny czas tego licznika.
     * @param timeUnit jednostka na wyjściu
     * @return czas tego licznika.
     */
    public long getCurrentTime(final TimeUnit timeUnit)
    {
        if (! this.isStarted())
        {
            return timeUnit.convert(this.stoppedTime, TimeUnit.MILLISECONDS);
        }

        if (this.isGrowing)
        {
            return timeUnit.convert(this.getTimeSpentAfterStart() + this.baseTime, TimeUnit.MILLISECONDS);
        }
        else
        {
            return timeUnit.convert(this.baseTime - this.getTimeSpentAfterStart(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Oblicza czas pozostały do momentu podanego w argumencie.
     *
     * Np. jesli licznik ma getCurrentTime = 1 MINUTES
     * To dla target=4 MINUTES
     * Wynikiem bedzie 3 MINUTES
     *
     * @param target moment do którego nalezy policzyc czas.
     * @param targetUnit jednostka argumentu.
     * @param resultUnit jednostka wynikowa.
     * @return czas do podanego momentu. Moze byc ujemny jesli juz byl.
     */
    public long calcTimeTo(final long target, final TimeUnit targetUnit, final TimeUnit resultUnit)
    {
        final long currTime = this.getCurrentTime(TimeUnit.MILLISECONDS);
        if (this.isGrowing)
        {
            final long result = targetUnit.toMillis(target) - currTime;
            return resultUnit.convert(result, TimeUnit.MILLISECONDS);
        }
        else
        {
            final long result = currTime - targetUnit.toMillis(target);
            return resultUnit.convert(result, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Oblicza czas w tickach do momentu podanego w argumencie.
     *
     * @see StaticTimer#calcTimeTo(long, TimeUnit, TimeUnit)
     */
    public long calcTimeToInTicks(final long target, final TimeUnit targetUnit)
    {
        return this.calcTimeTo(target, targetUnit, TimeUnit.MILLISECONDS) / ONE_TICK_MILIS;
    }

    public String humanReadableTimeAfterStart()
    {
        final long time = this.getCurrentTime(TimeUnit.SECONDS);
        final int minutes = (int) (time / 60);
        final int seconds = (int) (time - minutes * 60);

        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(this.isGrowing ? "/\\ " : "\\/ ");
        builder.append(this.humanReadableTimeAfterStart());

        return builder.toString();
    }
}
