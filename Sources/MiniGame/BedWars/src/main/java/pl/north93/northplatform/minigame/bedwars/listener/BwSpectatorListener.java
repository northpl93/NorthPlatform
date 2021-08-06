package pl.north93.northplatform.minigame.bedwars.listener;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorJoinEvent;
import pl.north93.northplatform.api.minigame.shared.api.PlayerStatus;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;

public class BwSpectatorListener implements AutoListener
{
    private static final Vector VECTOR_UP = new Vector(0, 2, 0);

    @EventHandler
    public void teleportOnDeath(final PlayerDeathEvent event)
    {
        final LocalArena arena = getArena(event.getEntity());
        final BedWarsArena arenaData = arena.getArenaData();

        final int minSpectatorY = arenaData.getConfig().getMinSpectatorY();
        this.teleportToSafeY(event.getEntity(), minSpectatorY);
    }

    @EventHandler
    public void keepY(final PlayerMoveEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());

        final PlayerStatus playerStatus = getPlayerStatus(player);
        if (playerStatus == null || ! playerStatus.isSpectator() || player.hasPermission("bedwars.ignoreYKeeper"))
        {
            return;
        }

        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (from.getBlockY() == to.getBlockY())
        {
            return;
        }

        final LocalArena arena = getArena(player);
        final BedWarsArena arenaData = arena.getArenaData();

        final int minSpectatorY = arenaData.getConfig().getMinSpectatorY();
        if (minSpectatorY <= to.getY())
        {
            return;
        }

        player.setFlying(true);

        if (minSpectatorY - to.getY() > 3)
        {
            this.teleportToSafeY(player, minSpectatorY);
        }
        else
        {
            player.setVelocity(VECTOR_UP);
        }
    }

    public void teleportToSafeY(final Player player, final int minSpectatorY)
    {
        final Location playerLocation = player.getLocation();
        final Location location;

        final Block highestBlockAt = player.getWorld().getHighestBlockAt(playerLocation.getBlockX(), playerLocation.getBlockZ());
        if (highestBlockAt.getY() > minSpectatorY)
        {
            location = highestBlockAt.getLocation();
        }
        else
        {
            playerLocation.setY(minSpectatorY);
            location = playerLocation;
        }

        player.teleport(location);
    }

    @EventHandler
    public void teleportSpectatorToLobby(final SpectatorJoinEvent event)
    {
        final LocalArena arena = getArena(event.getPlayer());
        assert arena != null;

        final BedWarsArena arenaData = arena.getArenaData();
        final Location location = arenaData.getConfig().getLobby().toBukkit(arena.getWorld().getCurrentWorld());
        event.getPlayer().teleport(location);
    }
}
