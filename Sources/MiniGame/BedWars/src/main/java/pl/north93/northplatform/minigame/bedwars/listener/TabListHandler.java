package pl.north93.northplatform.minigame.bedwars.listener;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import com.mojang.authlib.GameProfile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.protocol.wrappers.WrapperPlayOutPlayerInfo;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.PlayersManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.northplatform.minigame.bedwars.arena.Team;

@Slf4j
public class TabListHandler implements AutoListener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void prepareTabTeams(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();
        final PlayersManager playersManager = arena.getPlayersManager();

        for (final INorthPlayer player : playersManager.getPlayers())
        {
            final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
            assert playerData != null : "Data is set in previous GameStartEvent listener";

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

            log.debug("Created team {} for arena {}", teamName, arena.getId());

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
        final INorthPlayer player = INorthPlayer.wrap(event.getEntity());

        final LocalArena arena = getArena(player);
        assert arena != null;

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);

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
