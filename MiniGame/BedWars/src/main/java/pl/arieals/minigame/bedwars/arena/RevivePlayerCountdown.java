package pl.arieals.minigame.bedwars.arena;

import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.utils.AbstractCountdown;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class RevivePlayerCountdown extends AbstractCountdown
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;
    private final Player        player;
    private final BedWarsPlayer bedWarsPlayer;

    public RevivePlayerCountdown(final Player player, final BedWarsPlayer bedWarsPlayer)
    {
        super(5);
        this.player = player;
        this.bedWarsPlayer = bedWarsPlayer;
    }

    @Override
    protected void loop(final int time)
    {
        final String locale = this.player.spigot().getLocale();

        final String title = translateAlternateColorCodes(this.messages.getMessage(locale, "die.title"));
        final String subtitle = translateAlternateColorCodes(this.messages.getMessage(locale, "die.subtitle", time));

        this.player.sendTitle(new Title(title, subtitle, 0, 20, 0));
    }

    @Override
    protected void end()
    {
        if (! this.player.isOnline())
        {
            return;
        }

        this.bedWarsPlayer.setAlive(true);

        this.player.setAllowFlight(false);
        this.player.setVisible(true);
        this.player.sendMessage("Revived");
        this.player.teleport(this.bedWarsPlayer.getTeam().getSpawn());
    }
}
