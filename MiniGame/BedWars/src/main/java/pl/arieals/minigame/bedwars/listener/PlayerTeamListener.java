package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.setPlayerData;


import java.util.Comparator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;

public class PlayerTeamListener implements Listener
{
    @EventHandler
    public void playerJoin(final PlayerJoinArenaEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();
        final BedWarsPlayer playerData = new BedWarsPlayer(event.getPlayer());
        setPlayerData(event.getPlayer(), playerData);

        final Team smallestTeam = arenaData.getTeams().stream().sorted(Comparator.comparing(team -> team.getPlayers().size())).findFirst().orElse(null);
        playerData.switchTeam(smallestTeam);
    }

    @EventHandler
    public void playerLeave(final PlayerQuitArenaEvent event)
    {
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        final Team team = playerData.getTeam();
        if (team != null)
        {
            team.getPlayers().remove(event.getPlayer());
        }
    }
}
