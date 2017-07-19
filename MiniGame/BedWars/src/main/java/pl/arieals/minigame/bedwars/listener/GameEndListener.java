package pl.arieals.minigame.bedwars.listener;

import static pl.north93.zgame.api.bukkit.utils.ChatUtils.centerMessage;
import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.api.minigame.server.gamehost.reward.CurrencyReward;
import pl.arieals.api.minigame.server.gamehost.reward.IReward;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
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
            event.getArena().setGamePhase(GamePhase.POST_GAME); // todo temporary disable
        }
    }

    @EventHandler
    public void onGameEnd(final GameEndEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();
        final PlayersManager players = event.getArena().getPlayersManager();

        players.broadcast(this.messages, "separator");
        players.broadcast(this.messages, "end.header", MessageLayout.CENTER);

        for (final Team team : arenaData.getTeams())
        {
            final String teamNameKey = "team.scoreboard." + team.getName();
            final List<String> nicks = this.playersList(team);

            for (final Player player : team.getPlayers())
            {
                this.messages.sendMessage(player, teamNameKey, MessageLayout.CENTER);
                for (final String nick : nicks)
                {
                    player.sendMessage(nick);
                }
            }
        }

        players.broadcast(this.messages, "end.top_kills", MessageLayout.CENTER);

        final ArrayList<BedWarsPlayer> ranking = new ArrayList<>(arenaData.getPlayers());
        ranking.sort(Comparator.comparing(BedWarsPlayer::getKills));
        final Iterator<BedWarsPlayer> iterator = ranking.iterator();
        for (int i = 0; i < 3 && iterator.hasNext(); i++)
        {
            final BedWarsPlayer next = iterator.next();
            Bukkit.broadcastMessage((i + 1) + ". " + next.getBukkitPlayer().getDisplayName());
        }

        players.broadcast(this.messages, "separator");
        players.broadcast(this.messages, "end.rewards", MessageLayout.CENTER);
        for (final Player player : players.getPlayers())
        {
            final Map<String, List<IReward>> rewards = event.getArena().getRewards().groupRewardsOf(player);
            for (final Map.Entry<String, List<IReward>> entry : rewards.entrySet())
            {
                final String name = "reward." + entry.getKey();
                final double sum = entry.getValue().stream().map(ireward -> ((CurrencyReward) ireward)).mapToDouble(CurrencyReward::getAmount).sum();

                player.sendMessage(name + " " + sum);
            }
        }

        players.broadcast(this.messages, "separator");
    }

    private List<String> playersList(final Team team)
    {
        final List<String> nicks = new LinkedList<>();
        final Iterator<Player> playersIterator = team.getPlayers().iterator();
        while (playersIterator.hasNext())
        {
            final StringBuilder line = new StringBuilder();

            line.append("&7");
            line.append(playersIterator.next().getDisplayName());
            if (playersIterator.hasNext())
            {
                line.append(' ');
                line.append("&7");
                line.append(playersIterator.next().getDisplayName());
            }

            nicks.add(centerMessage(translateAlternateColorCodes(line.toString())));
        }
        return nicks;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
