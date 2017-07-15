package pl.arieals.minigame.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.TeamEliminatedEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class GameEndListener implements Listener
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @EventHandler
    public void onTeamEliminate(final TeamEliminatedEvent event)
    {
        final Team team = event.getEliminatedTeam();

        for (final Player player : event.getArena().getPlayersManager().getPlayers())
        {
            final String locale = player.spigot().getLocale();
            final String teamName = this.messages.getMessage(locale, "team.nominative." + team.getName());
            this.messages.sendMessage(player, "team_eliminated", MessageLayout.SEPARATED, team.getColorChar(), teamName);
        }

        final BedWarsArena arenaData = event.getArena().getArenaData();
        if (arenaData.getTeams().stream().filter(Team::isTeamAlive).count() <= 1)
        {
            //event.getArena().setGamePhase(GamePhase.POST_GAME); // todo temporary disable
        }
    }

    @EventHandler
    public void onGameEnd(final GameEndEvent event)
    {
        Bukkit.broadcastMessage("Koniec gry na " + event.getArena().getId());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}