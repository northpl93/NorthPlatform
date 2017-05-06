package pl.arieals.minigame.elytrarace.arena;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.cfg.Checkpoint;

public class ElytraRacePlayer
{
    private Checkpoint checkpoint;
    private Integer    points; // uzywane w SCORE_MODE

    public Checkpoint getCheckpoint()
    {
        return this.checkpoint;
    }

    public void setCheckpoint(final Checkpoint checkpoint)
    {
        this.checkpoint = checkpoint;
    }

    public Integer getPoints()
    {
        return this.points;
    }

    public void setPoints(final Integer points)
    {
        this.points = points;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("checkpoint", this.checkpoint).toString();
    }
}
