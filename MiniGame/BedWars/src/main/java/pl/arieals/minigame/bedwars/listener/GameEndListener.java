package pl.arieals.minigame.bedwars.listener;

import static org.diorite.utils.function.FunctionUtils.not;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.PlayerReconnectTimedOut;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.TeamEliminatedEvent;
import pl.north93.zgame.api.bukkit.utils.SimpleCountdown;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class GameEndListener implements Listener
{
    private static final int RECONNECT_TIMEOUT = 60 * 20;
    @Inject
    private Logger logger;
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

        this.logger.log(Level.INFO, "Team {0} eliminated on arena {1}", new Object[]{event.getEliminatedTeam().getName(), arena.getId()});

        final Team team = event.getEliminatedTeam();
        for (final Player player : team.getPlayers())
        {
            // jesli gracz jest offline to wtedy mogl nie dostac statusu wyeliminowanego
            // wiec recznie sie upewniamy, ze wszystko jest ok
            final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
            if (playerData == null)
            {
                continue;
            }
            playerData.eliminate();
        }

        final TranslatableString teamName = TranslatableString.of(this.messages, "@team.nominative." + team.getName());
        arena.getPlayersManager().broadcast(this.messages, "team_eliminated", MessageLayout.SEPARATED, team.getColorChar(), teamName);

        final BedWarsArena arenaData = arena.getArenaData();
        if (arenaData.getTeams().stream().filter(not(Team::isEliminated)).count() <= 1)
        {
            arena.endGame();
        }
    }

    @EventHandler
    public void endArenaWhenOnePlayerLeft(final PlayerQuitArenaEvent event)
    {
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);
        if (event.getArena().getGamePhase() != GamePhase.STARTED || playerData == null)
        {
            return;
        }

        final Team team = playerData.getTeam();
        final Object[] playerLogData = {event.getPlayer().getName(), event.getArena().getId()};

        if (team.isBedAlive())
        {
            // team ma lozko, ustawiamy timeout eliminacji gracza na 60 sekund.
            // Po tym czasie juz nie wroci do gry
            // Nic wiecej nie trzeba robic, gracz albo wroci do gry albo ktos mu zniszczy lozko.
            event.getArena().getScheduler().runTaskLater(new PlayerReconnectTimedOut(playerData), RECONNECT_TIMEOUT);
            this.logger.log(Level.INFO, "Player {0} has 60 seconds to return to the game on arena {1}.", playerLogData);
            return;
        }

        this.logger.log(Level.INFO, "Player {0} eliminated because he disconnected without bed on arena {1}", playerLogData);
        playerData.eliminate();
        team.checkEliminated();
    }

    @EventHandler
    public void scheduleArenaRestart(final GameEndEvent event)
    {
        final LocalArena arena = event.getArena();

        final SimpleCountdown restartCountdown = new SimpleCountdown(200).endCallback(arena::prepareNewCycle);
        arena.getScheduler().runSimpleCountdown(restartCountdown);
    }

    @EventHandler
    public void onGameEnd(final GameEndEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();
        final PlayersManager players = event.getArena().getPlayersManager();

        players.broadcast(this.messages, "separator");
        players.broadcast(this.messages, "end.header", MessageLayout.CENTER);

        final Optional<Team> winner = arenaData.getTeams().stream().filter(not(Team::isEliminated)).findAny();
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
