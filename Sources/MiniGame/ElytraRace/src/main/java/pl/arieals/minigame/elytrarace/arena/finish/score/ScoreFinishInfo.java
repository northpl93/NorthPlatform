package pl.arieals.minigame.elytrarace.arena.finish.score;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.arena.finish.FinishInfo;

public class ScoreFinishInfo extends FinishInfo
{
    private final int points;

    public ScoreFinishInfo(final UUID uuid, final String displayName, final int points)
    {
        super(uuid, displayName);
        this.points = points;
    }

    public int getPoints()
    {
        return this.points;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("points", this.points).toString();
    }
}
