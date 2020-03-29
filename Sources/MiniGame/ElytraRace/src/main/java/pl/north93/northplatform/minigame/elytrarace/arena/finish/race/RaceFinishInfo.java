package pl.north93.northplatform.minigame.elytrarace.arena.finish.race;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.elytrarace.arena.finish.FinishInfo;

public class RaceFinishInfo extends FinishInfo
{
    private final long time;
    private final int place;

    public RaceFinishInfo(final UUID uuid, final String displayName, final long time, final int place)
    {
        super(uuid, displayName);
        this.time = time;
        this.place = place;
    }

    public long getTime()
    {
        return this.time;
    }

    public int getPlace()
    {
        return this.place;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("time", this.time).append("place", this.place).toString();
    }
}
