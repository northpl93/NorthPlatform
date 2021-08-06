package pl.north93.northplatform.minigame.bedwars.listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.ArenaChatManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.reward.CurrencyReward;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.northplatform.minigame.bedwars.arena.PlayerReconnectTimedOut;
import pl.north93.northplatform.minigame.bedwars.arena.Team;
import pl.north93.northplatform.minigame.bedwars.event.TeamEliminatedEvent;

@Slf4j
public class GameEndListener implements AutoListener
{
    private static final int RECONNECT_TIMEOUT = 60 * 20;
    @Inject
    private IStatisticsManager statisticsManager;
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

        log.info("Team {} eliminated on arena {}", event.getEliminatedTeam().getName(), arena.getId());

        final Team team = event.getEliminatedTeam();
        for (final BedWarsPlayer playerData : team.getPlayers())
        {
            // jesli gracz jest offline to wtedy mogl nie dostac statusu wyeliminowanego
            // wiec recznie sie upewniamy, ze wszystko jest ok
            playerData.eliminate();
        }

        final TranslatableString teamName = TranslatableString.of(this.messages, "@team.nominative." + team.getName());
        arena.getChatManager().broadcast(this.messages, "team_eliminated", MessageLayout.SEPARATED, team.getColor(), teamName);

        final BedWarsArena arenaData = arena.getArenaData();
        final Predicate<Team> isEliminated = Team::isEliminated;
        if (arenaData.getTeams().stream().filter(isEliminated.negate()).count() <= 1)
        {
            arena.endGame();
        }
    }

    @EventHandler
    public void endArenaWhenOnePlayerLeft(final PlayerQuitArenaEvent event)
    {
        final INorthPlayer player = event.getPlayer();

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (event.getArena().getGamePhase() != GamePhase.STARTED || playerData == null)
        {
            return;
        }

        final Team team = playerData.getTeam();
        final Object[] playerLogData = {player.getName(), event.getArena().getId()};

        if (team.isBedAlive())
        {
            // team ma lozko, ustawiamy timeout eliminacji gracza na 60 sekund.
            // Po tym czasie juz nie wroci do gry
            // Nic wiecej nie trzeba robic, gracz albo wroci do gry albo ktos mu zniszczy lozko.
            event.getArena().getScheduler().runTaskLater(new PlayerReconnectTimedOut(playerData), RECONNECT_TIMEOUT);
            log.info("Player {} has 60 seconds to return to the game on arena {}.", playerLogData);
            return;
        }

        log.info("Player {} eliminated because he disconnected without bed on arena {}", playerLogData);
        playerData.eliminate();
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
        final LocalArena arena = event.getArena();

        final BedWarsArena arenaData = arena.getArenaData();
        final ArenaChatManager chatManager = arena.getChatManager();

        chatManager.broadcast(this.messages, "separator");
        chatManager.broadcast(this.messages, "end.header", MessageLayout.CENTER);

        final Predicate<Team> isEliminatedPredicate = Team::isEliminated;
        arenaData.getTeams().stream().filter(isEliminatedPredicate.negate()).findAny().ifPresent(winner ->
        {
            // obslugujemy zwyciestwo danego teamu (nagrody, licznik zwyciestw)
            this.handleTeamWin(arena, arenaData, winner);

            final TranslatableString teamNameKey = TranslatableString.of(this.messages, "@team.scoreboard." + winner.getName());
            final String nicks = this.playersList(winner);

            chatManager.broadcast(this.messages, "end.winner_list", MessageLayout.CENTER, winner.getColor(), teamNameKey, nicks);
        });

        chatManager.broadcast(this.messages, "end.top_kills", MessageLayout.CENTER);

        final ArrayList<BedWarsPlayer> ranking = new ArrayList<>(arenaData.getPlayers());
        ranking.sort(Comparator.comparing(BedWarsPlayer::getKills).reversed());
        final Iterator<BedWarsPlayer> iterator = ranking.iterator();
        for (int i = 0; i < 3 && iterator.hasNext(); i++)
        {
            final BedWarsPlayer next = iterator.next();
            final int place = i + 1;
            chatManager.broadcast(this.messages, "end.place." + place, MessageLayout.CENTER, next.getBukkitPlayer().getDisplayName(), next.getKills());
        }

        chatManager.broadcast(this.messages, "empty_line");
        chatManager.broadcast(this.messages, "separator");
        chatManager.broadcast(this.messages, "end.rewards", MessageLayout.CENTER);
        for (final Player player : arena.getPlayersManager().getPlayers())
        {
            arena.getRewards().renderRewards(this.messages, player);
        }

        chatManager.broadcast(this.messages, "separator");
    }

    private String playersList(final Team team)
    {
        final StringBuilder nicks = new StringBuilder();
        final Iterator<INorthPlayer> playersIterator = team.getBukkitPlayers().iterator();
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

    private void handleTeamWin(final LocalArena arena, final BedWarsArena bedWarsArena, final Team team)
    {
        final HigherNumberBetterStatistic winsStat = new HigherNumberBetterStatistic("bedwars/wins");
        final NumberUnit numberUnit = new NumberUnit(1L);

        final int currencyAmount = bedWarsArena.getBedWarsConfig().getRewards().getWin();
        final CurrencyReward reward = new CurrencyReward("win", "minigame", currencyAmount);

        for (final Player player : team.getBukkitPlayers())
        {
            final IStatisticHolder holder = this.statisticsManager.getPlayerHolder(player.getUniqueId());
            holder.incrementRecord(winsStat, numberUnit);

            arena.getRewards().addReward(Identity.of(player), reward);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
