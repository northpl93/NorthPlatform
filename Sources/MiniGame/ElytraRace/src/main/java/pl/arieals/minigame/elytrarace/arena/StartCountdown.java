package pl.arieals.minigame.elytrarace.arena;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import com.destroystokyo.paper.Title;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.bukkit.utils.AbstractCountdown;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

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

            final BaseComponent message = this.msg.getMessage(locale, "start_countdown", time);
            message.setColor(color.asBungee());
            final Title title = new Title(message, null, 0, 21, 0);

            player.sendTitle(title);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HAT, 1, 0);
        }
    }

    @Override
    protected void end()
    {
        final ElytraRaceArena data = this.arena.getArenaData();
        if (data == null)
        {
            // dzieje sie tak gdy podczas odliczania przelaczymy na initialising/lobby
            return;
        }

        data.setStarted(true);

        for (final Player player : this.arena.getPlayersManager().getPlayers())
        {
            final String message = this.msg.getString(player.getLocale(), "start");
            final Title title = new Title(message, "", 0, 20, 0);
            player.sendTitle(title);

            player.setFlying(false);
            player.setAllowFlight(false);
            player.setGliding(true);
            
            player.teleport(getPlayerData(player, ElytraRacePlayer.class).getStartLocation());
            final Vector direction = player.getLocation().getDirection();
            direction.multiply(0.7);

            player.setVelocity(direction);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 0);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).toString();
    }
}
