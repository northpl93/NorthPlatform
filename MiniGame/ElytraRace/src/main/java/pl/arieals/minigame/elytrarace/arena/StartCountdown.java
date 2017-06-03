package pl.arieals.minigame.elytrarace.arena;

import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.utils.AbstractCountdown;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class StartCountdown extends AbstractCountdown
{
    private final LocalArena arena;
    @Inject
    @Messages("ElytraRace")
    private MessagesBox msg;

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
            final String locale = player.spigot().getLocale();

            final String message = this.msg.getMessage(locale, "start_countdown", time);
            final Title title = new Title(message, "", 0, 21, 0);

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
