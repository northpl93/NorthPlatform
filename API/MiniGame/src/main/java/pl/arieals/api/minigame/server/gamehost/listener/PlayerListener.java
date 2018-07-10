package pl.arieals.api.minigame.server.gamehost.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.zgame.api.bukkit.player.INorthPlayer.asCraftPlayer;
import static pl.north93.zgame.api.bukkit.player.INorthPlayer.wrap;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;
import static pl.north93.zgame.api.global.utils.lang.JavaUtils.instanceOf;


import java.util.Optional;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.player.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinWithoutArenaEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.arieals.api.minigame.shared.api.status.InGameStatus;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class PlayerListener implements AutoListener
{
    @Inject
    private MiniGameServer       server;
    @Inject
    private IBukkitExecutor      bukkitExecutor;
    @Inject
    private IPlayerStatusManager statusManager;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final INorthPlayer player = wrap(event.getPlayer());

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (arena.isPresent())
        {
            final LocalArena localArena = arena.get();

            final PlayersManager playersManager = localArena.getPlayersManager();
            playersManager.playerConnected(player);

            this.setPlayerStatus(player, localArena);
            this.fixLobbyVisibility(player, localArena);
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
    private void fixLobbyVisibility(final Player player, final LocalArena arena)
    {
        final EntityPlayer joiningPlayer = asCraftPlayer(player).getHandle();
        final EntityTrackerEntry joiningTrackerEntry = getTrackerEntry(joiningPlayer);
        if (joiningTrackerEntry == null)
        {
            return;
        }

        for (final Player arenaPlayer : arena.getPlayersManager().getPlayers())
        {
            final EntityPlayer arenaEntityPlayer = asCraftPlayer(arenaPlayer).getHandle();
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

    private void setPlayerStatus(final Player player, final LocalArena arena)
    {
        final GameHostManager gameHostManager = this.server.getServerManager();
        this.bukkitExecutor.async(() ->
        {
            final Identity identity = Identity.of(player);
            final InGameStatus status = new InGameStatus(gameHostManager.getServerId(), arena.getId(), arena.getMiniGame());

            this.statusManager.updatePlayerStatus(identity, status);
        });
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final INorthPlayer player = wrap(event.getPlayer());

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (! arena.isPresent())
        {
            return;
        }

        final PlayersManager playersManager = arena.get().getPlayersManager();
        playersManager.playerDisconnected(player);
    }

    @EventHandler
    public void blockPlayerDamageOutsideGame(final EntityDamageEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        if (player == null)
        {
            return;
        }

        final LocalArena arena = getArena(player);
        if (arena == null || arena.getGamePhase() != GamePhase.STARTED)
        {
            event.setCancelled(true);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
