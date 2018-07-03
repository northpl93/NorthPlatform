package pl.arieals.minigame.elytrarace.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.cfg.Checkpoint;
import pl.arieals.minigame.elytrarace.shop.effects.IElytraEffect;

public class ElytraRacePlayer
{
    private Player        player;
    private Location      startLocation;
    private boolean       isDev;
    private boolean       finished;
    private Checkpoint    checkpoint;
    private IElytraEffect effect;

    public ElytraRacePlayer(final Player player, final IElytraEffect effect, final Location startLocation)
    {
        this.player = player;
        this.effect = effect;
        this.startLocation = startLocation;
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public IElytraEffect getEffect()
    {
        return this.effect;
    }

    public Location getStartLocation()
    {
        return this.startLocation;
    }

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

    public ElytraScorePlayer asScorePlayer()
    {
        return (ElytraScorePlayer) this;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("startLocation", this.startLocation).append("isDev", this.isDev).append("finished", this.finished).append("checkpoint", this.checkpoint).append("effect", this.effect).toString();
    }
}
