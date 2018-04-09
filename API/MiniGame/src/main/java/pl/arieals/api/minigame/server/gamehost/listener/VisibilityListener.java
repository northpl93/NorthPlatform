package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinWithoutArenaEvent;

public class VisibilityListener implements Listener
{
    @EventHandler
    public void playerJoinArena(final PlayerJoinArenaEvent event)
    {
        final List<Player> arenaPlayers = event.getArena().getPlayersManager().getPlayers();
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            if (arenaPlayers.contains(player))
            {
                continue;
            }

            for (final Player arenaPlayer : arenaPlayers)
            {
                arenaPlayer.hidePlayer(player);
                player.hidePlayer(arenaPlayer);
            }
        }
    }

    @EventHandler
    public void playerWithoutArenaJoin(final PlayerJoinWithoutArenaEvent event)
    {
        final Player joiningPlayer = event.getPlayer();
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            // nie ma potrzeby sprawdzania czy nie ukrywamy siebie dla siebie
            // bo to juz jest zaimplementowane w hidePlayer()
            player.hidePlayer(joiningPlayer);
            joiningPlayer.hidePlayer(player);
        }

        joiningPlayer.sendMessage(ChatColor.RED + "Wchodzisz na serwer hostujacy gre bez powiazania z zadna arena!");
    }
}
