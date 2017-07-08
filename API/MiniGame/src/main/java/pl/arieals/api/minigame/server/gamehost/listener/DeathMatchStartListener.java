package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.ArenaWorld;
import pl.arieals.api.minigame.server.gamehost.deathmatch.ArenaDestroyerTask;
import pl.arieals.api.minigame.server.gamehost.event.arena.DeathMatchPrepareEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class DeathMatchStartListener implements Listener
{
    @Inject
    private MiniGameServer server;
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private JavaPlugin     plugin;

    @EventHandler
    public void onDeathMatchStart(final DeathMatchPrepareEvent event)
    {
        final GameHostManager serverManager = this.server.getServerManager();

        // usuwamy wszystkie sledzone regiony z starego swiata
        serverManager.getRegionManager().getRegions(event.getOldWorld()).forEach(ITrackedRegion::unTrack);

        final ArenaWorld arenaWorld = event.getArena().getWorld();

        final double x = Double.valueOf(arenaWorld.getProperty("loc-x"));
        final double y = Double.valueOf(arenaWorld.getProperty("loc-y"));
        final double z = Double.valueOf(arenaWorld.getProperty("loc-z"));
        final Location location = new Location(arenaWorld.getCurrentWorld(), x, y, z);

        for (final Player player : event.getArena().getPlayersManager().getPlayers())
        {
            this.messages.sendMessage(player, "deathmatch.welcome", MessageLayout.CENTER);
            player.teleport(location);
        }

        final int removerX = Integer.valueOf(arenaWorld.getProperty("remover-x"));
        final int removerY = Integer.valueOf(arenaWorld.getProperty("remover-y"));
        final int removerZ = Integer.valueOf(arenaWorld.getProperty("remover-z"));

        new ArenaDestroyerTask(new Location(arenaWorld.getCurrentWorld(), removerX, removerY, removerZ), event.getArena()).runTaskTimer(this.plugin, 100, 10);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
