package pl.arieals.minigame.bedwars.listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.TeamEliminatedEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class GameEndListener implements Listener
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @EventHandler
    public void onTeamEliminate(final TeamEliminatedEvent event)
    {
        final LocalArena arena = event.getArena();
        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            // sprawdzamy czy mamy dobry gamephase, bo mozemy tu spowodowac przelaczenie areny
            // z trybu initialising spowrotem do post_game i spowodowac tym samym wyjatek.
            return;
        }

        final Team team = event.getEliminatedTeam();

        final TranslatableString teamName = TranslatableString.of(this.messages, "@team.nominative." + team.getName());
        arena.getPlayersManager().broadcast(this.messages, "team_eliminated", MessageLayout.SEPARATED, team.getColorChar(), teamName);

        final BedWarsArena arenaData = arena.getArenaData();
        if (arenaData.getTeams().stream().filter(Team::isTeamAlive).count() <= 1)
        {
            arena.setGamePhase(GamePhase.POST_GAME);
        }
    }

    @EventHandler
    public void onGameEnd(final GameEndEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();
        final PlayersManager players = event.getArena().getPlayersManager();

        players.broadcast(this.messages, "separator");
        players.broadcast(this.messages, "end.header", MessageLayout.CENTER);

        final Optional<Team> winner = arenaData.getTeams().stream().filter(Team::isTeamAlive).findAny();
        if (winner.isPresent())
        {
            final TranslatableString teamNameKey = TranslatableString.of(this.messages, "@team.scoreboard." + winner.get().getName());
            final String nicks = this.playersList(winner.get());

            players.broadcast(this.messages, "end.winner_list", MessageLayout.CENTER, winner.get().getColorChar(), teamNameKey, nicks);
        }

        players.broadcast(this.messages, "end.top_kills", MessageLayout.CENTER);

        final ArrayList<BedWarsPlayer> ranking = new ArrayList<>(arenaData.getPlayers());
        ranking.sort(Comparator.comparing(BedWarsPlayer::getKills).reversed());
        final Iterator<BedWarsPlayer> iterator = ranking.iterator();
        for (int i = 0; i < 3 && iterator.hasNext(); i++)
        {
            final BedWarsPlayer next = iterator.next();
            final int place = i + 1;
            players.broadcast(this.messages, "end.place." + place, MessageLayout.CENTER, next.getBukkitPlayer().getDisplayName(), next.getKills());
        }

        players.broadcast(this.messages, "empty_line");
        players.broadcast(this.messages, "separator");
        players.broadcast(this.messages, "end.rewards", MessageLayout.CENTER);
        for (final Player player : players.getPlayers())
        {
            event.getArena().getRewards().renderRewards(this.messages, player);
        }

        players.broadcast(this.messages, "separator");
    }

    private String playersList(final Team team)
    {
        final StringBuilder nicks = new StringBuilder();
        final Iterator<Player> playersIterator = team.getPlayers().iterator();
        while (playersIterator.hasNext())
        {
            nicks.append("&7");
            nicks.append(playersIterator.next().getDisplayName());
            if (playersIterator.hasNext())
            {
                nicks.append(' ');
                nicks.append("&7");
                nicks.append(playersIterator.next().getDisplayName());
            }

            if (playersIterator.hasNext())
            {
                nicks.append("\n");
            }
        }
        return nicks.toString();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
