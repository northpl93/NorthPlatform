package pl.arieals.minigame.elytrarace.arena;

import static org.bukkit.ChatColor.translateAlternateColorCodes;


import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import com.destroystokyo.paper.Title;

import org.bukkit.ChatColor;
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

            final ChatColor color;
            if (time < 4)
            {
                color = ChatColor.RED;
            }
            else if (time < 6)
            {
                color = ChatColor.YELLOW;
            }
            else
            {
                color = ChatColor.GREEN;
            }

            final String message = this.msg.getMessage(locale, "start_countdown", time);
            final Title title = new Title(color + message, "", 0, 21, 0);

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
            final String message = translateAlternateColorCodes('&', this.msg.getMessage(player.spigot().getLocale(), "start"));
            final Title title = new Title(message, "", 0, 20, 0);
            player.sendTitle(title);

            player.setFlying(false);
            player.setAllowFlight(false);
            player.setGliding(true);
            
            player.teleport(getPlayerData(player, ElytraRacePlayer.class).getStartLocation());
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
