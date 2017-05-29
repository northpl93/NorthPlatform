package pl.arieals.minigame.elytrarace.arena;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.cfg.Checkpoint;

public class ElytraRacePlayer
{
    private boolean    isDev;
    private boolean    finished;
    private Checkpoint checkpoint;

    public boolean isDev()
    {
        return this.isDev;
    }

    public void setDev(final boolean dev)
    {
        this.isDev = dev;
    }

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

    public int getCheckpointNumber()
    {
        final int checkpointNumber;
        if (this.checkpoint == null)
        {
            checkpointNumber = 0;
        }
        else
        {
            checkpointNumber = this.checkpoint.getNumber();
        }
        return checkpointNumber;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("isDev", this.isDev).append("finished", this.finished).append("checkpoint", this.checkpoint).toString();
    }
}
