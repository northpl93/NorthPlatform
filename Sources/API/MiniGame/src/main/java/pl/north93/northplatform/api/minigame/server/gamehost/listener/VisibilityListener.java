package pl.north93.northplatform.api.minigame.server.gamehost.listener;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinWithoutArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorJoinEvent;

public class VisibilityListener implements AutoListener
{
    @Inject
    private IBukkitServerManager serverManager;

    @EventHandler
    public void playerJoinArena(final PlayerJoinArenaEvent event)
    {
        this.updateVisibility(event);
    }

    @EventHandler
    public void spectatorJoinArena(final SpectatorJoinEvent event)
    {
        this.updateVisibility(event);
    }

    private void updateVisibility(final PlayerArenaEvent event)
    {
        final JavaPlugin plugin = this.serverManager.getPlugin();

        final Set<INorthPlayer> arenaPlayers = event.getArena().getPlayersManager().getAllPlayers();
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            //noinspection SuspiciousMethodCalls NorthPlayerImpl deleguje equals i hashCode do CraftPlayer
            if (arenaPlayers.contains(player))
            {
                continue;
            }

            for (final INorthPlayer arenaPlayer : arenaPlayers)
            {
                final Player craftPlayer = arenaPlayer.getCraftPlayer(); // ClassCastException przez NorthPlayerImpl

                craftPlayer.hidePlayer(plugin, player);
                player.hidePlayer(plugin, craftPlayer);
            }
        }
    }

    @EventHandler
    public void playerWithoutArenaJoin(final PlayerJoinWithoutArenaEvent event)
    {
        final Player joiningPlayer = event.getPlayer();
        joiningPlayer.setGameMode(GameMode.CREATIVE);
        joiningPlayer.setCollidable(false);

        final JavaPlugin plugin = this.serverManager.getPlugin();
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            // nie ma potrzeby sprawdzania czy nie ukrywamy siebie dla siebie
            // bo to juz jest zaimplementowane w hidePlayer()
            player.hidePlayer(plugin, joiningPlayer);
        }

        joiningPlayer.sendMessage(ChatColor.RED + "Wchodzisz na serwer hostujacy gre bez powiazania z zadna arena!");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
