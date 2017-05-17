package pl.arieals.minigame.elytrarace.arena;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.cfg.Checkpoint;

public class ElytraRacePlayer
{
    private boolean    finished;
    private Checkpoint checkpoint;

    public boolean isFinished()
    {
        return this.finished;
    }

    public void setFinished(final boolean finished)
    {
        this.finished = finished;
    }

    public Checkpoint getCheckpoint()
    {
        return this.checkpoint;
    }

    public void setCheckpoint(final Checkpoint checkpoint)
    {
        this.checkpoint = checkpoint;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("finished", this.finished).append("checkpoint", this.checkpoint).toString();
    }
}
