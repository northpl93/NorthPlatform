package pl.arieals.api.minigame.server.gamehost.listener;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;


import java.util.Optional;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.EntityTrackerEntry;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinWithoutArenaEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerListener implements Listener
{
    @Inject
    private MiniGameServer server;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (arena.isPresent())
        {
            final LocalArena localArena = arena.get();

            final PlayersManager playersManager = localArena.getPlayersManager();
            playersManager.playerConnected(player);

            this.fixLobbyVisibility(localArena, player);
        }
        else
        {
            Bukkit.getPluginManager().callEvent(new PlayerJoinWithoutArenaEvent(player));
        }
    }

    /*
     * Gracze nie widza sie w lobby.
     *
     * Jest to spowodowane niepoprawnym dzialaniem
     * entitytrackera (lub gubionych pakietow podczas
     * przelaczania serwera w bungee, uj wie)
     *
     * Recznie untrackujemy kazdego wchodzacego gracza
     * zeby serwer mogl go ztrackowac ponownie, poprawnie
     */
    private void fixLobbyVisibility(final LocalArena arena, final Player player)
    {
        final EntityPlayer joiningPlayer = ((CraftPlayer) player).getHandle();
        final EntityTrackerEntry joiningTrackerEntry = getTrackerEntry(joiningPlayer);
        if (joiningTrackerEntry == null)
        {
            return;
        }

        for (final Player arenaPlayer : arena.getPlayersManager().getPlayers())
        {
            final EntityPlayer arenaEntityPlayer = ((CraftPlayer) arenaPlayer).getHandle();
            final EntityTrackerEntry arenaTrackerEntry = getTrackerEntry(arenaEntityPlayer);
            if (arenaTrackerEntry == null)
            {
                continue;
            }

            // untrackujemy wchodzacego gracza z trackera gracza bedacego juz na arenie
            arenaTrackerEntry.a(joiningPlayer); // if (this.trackedPlayers.contains(entityplayer)) {

            // untrackujemy gracza bedacego na arenie z trackera gracza wchodzacego na arene
            joiningTrackerEntry.a(arenaEntityPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (! arena.isPresent())
        {
            return;
        }

        final PlayersManager playersManager = arena.get().getPlayersManager();
        playersManager.playerDisconnected(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
