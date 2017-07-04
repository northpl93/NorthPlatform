package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.minigame.bedwars.utils.PlayerTeamPredicates.isInTeam;
import static pl.arieals.minigame.bedwars.utils.PlayerTeamPredicates.notInTeam;
import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import java.util.function.Predicate;

import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.BedDestroyedEvent;
import pl.arieals.minigame.bedwars.event.TeamEliminatedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class BedDestroyListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject @Messages("BedWars")
    private MessagesBox   messages;

    @EventHandler
    public void onBedDestroy(final BedDestroyedEvent event)
    {
        final BedWarsPlayer playerData = getPlayerData(event.getDestroyer(), BedWarsPlayer.class);
        final LocalArena arena = event.getArena();
        final Team team = event.getTeam();

        final PlayersManager playersManager = arena.getPlayersManager();
        playersManager.broadcast(isInTeam(team), this.messages,
                "bed_destroyed.you", MessageLayout.SEPARATED,
                team.getColor().getChar(),
                playerData.getTeam().getColor().getChar(),
                playerData.getBukkitPlayer().getDisplayName());

        final Predicate<Player> notInTeamPredicate = notInTeam(team);
        for (final Player player : playersManager.getPlayers())
        {
            if (! notInTeamPredicate.test(player))
            {
                continue;
            }
            final String locale = player.spigot().getLocale();
            final String teamName = this.messages.getMessage(locale, "team.genitive." + team.getName());

            this.messages.sendMessage(player, "bed_destroyed.global", MessageLayout.SEPARATED, team.getColorChar(), teamName, playerData.getTeam().getColorChar(), playerData.getBukkitPlayer().getDisplayName());
        }

        for (final Player player : team.getPlayers())
        {
            final String locale = player.spigot().getLocale();

            final String title = translateAlternateColorCodes(this.messages.getMessage(locale, "bed_destroyed.title.title"));
            final String subtitle = translateAlternateColorCodes(this.messages.getMessage(locale, "bed_destroyed.title.subtitle"));

            player.sendTitle(new Title(title, subtitle, 20, 20, 20));
        }

        if (! team.isTeamAlive())
        {
            this.apiCore.callEvent(new TeamEliminatedEvent(arena, team));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
