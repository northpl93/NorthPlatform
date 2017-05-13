package pl.arieals.minigame.elytrarace.arena;

import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.utils.SimpleCountdown;

public class StartCountdown extends SimpleCountdown
{
    private final LocalArena arena;

    public StartCountdown(final int time, final LocalArena arena)
    {
        super(time);
        this.arena = arena;
    }

    @Override
    protected void loop(final int time)
    {
        for (final Player player : this.arena.getPlayersManager().getPlayers())
        {
            final Title title = new Title("Start za " + time, "", 0, 21, 0);
            player.sendTitle(title);
        }
    }

    @Override
    protected void end()
    {
        final ElytraRaceArena data = this.arena.getArenaData();
        data.setStarted(true);

        for (final Player player : this.arena.getPlayersManager().getPlayers())
        {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setGliding(true);
            final Vector direction = player.getLocation().getDirection();
            direction.multiply(0.7);

            player.setVelocity(direction);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).toString();
    }
}
