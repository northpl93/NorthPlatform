package pl.north93.northplatform.api.minigame.server.gamehost.listener;

import static pl.north93.northplatform.api.bukkit.player.INorthPlayer.asCraftPlayer;
import static pl.north93.northplatform.api.bukkit.player.INorthPlayer.wrap;
import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;
import static pl.north93.northplatform.api.global.utils.lang.JavaUtils.instanceOf;


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

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.PlayersManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinWithoutArenaEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.north93.northplatform.api.minigame.shared.api.status.InGameStatus;

public class PlayerListener implements AutoListener
{
    @Inject
    private GameHostManager gameHostManager;
    @Inject
    private LocalArenaManager localArenaManager;
    @Inject
    private IBukkitExecutor bukkitExecutor;
    @Inject
    private IPlayerStatusManager statusManager;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final INorthPlayer player = wrap(event.getPlayer());

        this.localArenaManager.getArenaAssociatedWith(player.getUniqueId()).ifPresentOrElse(arena ->
        {
            final PlayersManager playersManager = arena.getPlayersManager();
            playersManager.playerConnected(player);

            this.setPlayerStatus(player, arena);
            this.fixLobbyVisibility(player, arena);
        }, () -> Bukkit.getPluginManager().callEvent(new PlayerJoinWithoutArenaEvent(player)));
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
        this.bukkitExecutor.async(() ->
        {
            final Identity identity = Identity.of(player);
            final InGameStatus status = new InGameStatus(this.gameHostManager.getServerId(), arena.getId(), arena.getMiniGame());

            this.statusManager.updatePlayerStatus(identity, status);
        });
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final INorthPlayer player = wrap(event.getPlayer());

        this.localArenaManager.getArenaAssociatedWith(player.getUniqueId()).ifPresent(arena ->
        {
            final PlayersManager playersManager = arena.getPlayersManager();
            playersManager.playerDisconnected(player);
        });
    }

    @EventHandler
    public void blockPlayerDamageOutsideGame(final EntityDamageEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        if (player == null)
        {
            return;
        }

        final LocalArena arena = MiniGameApi.getArena(player);
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
