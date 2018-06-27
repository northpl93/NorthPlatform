package pl.arieals.api.minigame.server.gamehost.deathmatch;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.world.ArenaWorld;
import pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchFightStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchLoadedEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class DeathMatchStartListener implements AutoListener
{
    @Inject
    private MiniGameServer server;
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private JavaPlugin     plugin;

    @EventHandler
    public void onDeathMatchLoaded(final DeathMatchLoadedEvent event)
    {
        final GameHostManager serverManager = this.server.getServerManager();

        // usuwamy wszystkie sledzone regiony z starego swiata
        serverManager.getRegionManager().getRegions(event.getOldWorld()).forEach(ITrackedRegion::unTrack);

        final Location location = event.getArena().getDeathMatch().getArenaSpawn();
        for (final Player player : event.getArena().getPlayersManager().getAllPlayers())
        {
            this.messages.sendMessage(player, "separator");
            this.messages.sendMessage(player, "deathmatch.welcome", MessageLayout.CENTER);
            this.messages.sendMessage(player, "separator");
            player.teleport(location);
        }
    }

    @EventHandler
    public void onFightStart(final DeathMatchFightStartEvent event)
    {
        final LocalArena arena = event.getArena();
        final ArenaWorld world = arena.getWorld();

        final int removerX = Integer.valueOf(world.getProperty("remover-x"));
        final int removerY = Integer.valueOf(world.getProperty("remover-y"));
        final int removerZ = Integer.valueOf(world.getProperty("remover-z"));

        final ArenaDestroyerTask task = new ArenaDestroyerTask(new Location(world.getCurrentWorld(), removerX, removerY, removerZ), arena);
        arena.getScheduler().runTaskTimer(task, 0, 10);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
