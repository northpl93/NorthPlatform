package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import com.mojang.authlib.GameProfile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.north93.zgame.api.bukkit.packets.wrappers.WrapperPlayOutPlayerInfo;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class TabListHandler implements Listener
{
    @Inject
    private Logger logger;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void prepareTabTeams(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();
        final PlayersManager playersManager = arena.getPlayersManager();

        for (final Player player : playersManager.getPlayers())
        {
            final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);

            for (final Player scoreboardUpdate : playersManager.getPlayers())
            {
                final Scoreboard scoreboard = scoreboardUpdate.getScoreboard();

                final org.bukkit.scoreboard.Team sbTeam = this.getOrCreateTeam(scoreboard, arena, playerData.getTeam());
                sbTeam.addEntry(player.getName());
            }
        }
    }

    public org.bukkit.scoreboard.Team getOrCreateTeam(final Scoreboard scoreboard, final LocalArena arena, final Team team)
    {
        final String teamName = arena.getId().toString().substring(0, 15) + team.getScoreboardOrder();

        final org.bukkit.scoreboard.Team result = scoreboard.getTeam(teamName);
        if (result == null)
        {
            final org.bukkit.scoreboard.Team newTeam = scoreboard.registerNewTeam(teamName);
            newTeam.setPrefix(team.getColor().toString());
            newTeam.setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);

            this.logger.log(Level.FINE, "Created team {0} for arena {1}", new Object[]{teamName, arena.getId()});

            return newTeam;
        }
        return result;
    }

    @EventHandler
    public void removePlayerWhenExit(final PlayerQuitArenaEvent event)
    {
        this.removeFromScoreboard(event.getArena(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void removeEliminatedPlayer(final PlayerDeathEvent event)
    {
        final Player player = event.getEntity();
        final LocalArena arena = getArena(player);
        assert arena != null;
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);

        if (playerData == null || playerData.isEliminated())
        {
            final WrapperPlayOutPlayerInfo removeWrapper = new WrapperPlayOutPlayerInfo(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER));
            removeWrapper.addPlayerData(new GameProfile(player.getUniqueId(), null), 0, null, null);

            for (final Player arenaPlayer : arena.getPlayersManager().getPlayers())
            {
                removeWrapper.sendTo(arenaPlayer);
            }
        }
    }

    private void removeFromScoreboard(final LocalArena arena, final Player playerToRemove)
    {
        final PlayersManager playersManager = arena.getPlayersManager();

        for (final Player player : playersManager.getPlayers())
        {
            final Scoreboard scoreboard = player.getScoreboard();

            for (final org.bukkit.scoreboard.Team team : scoreboard.getTeams())
            {
                team.removeEntry(playerToRemove.getName()); // zwroci false jak sie nie uda
            }
        }
    }
}
