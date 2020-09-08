package pl.north93.northplatform.api.minigame.server.gamehost.deathmatch;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.world.ArenaWorld;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchFightStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchLoadedEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.region.ITrackedRegion;

public class DeathMatchStartListener implements AutoListener
{
    @Inject
    private GameHostManager gameHostManager;
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;
    @Inject
    private JavaPlugin plugin;

    @EventHandler
    public void onDeathMatchLoaded(final DeathMatchLoadedEvent event)
    {
        // usuwamy wszystkie sledzone regiony z starego swiata
        this.gameHostManager.getRegionManager().getRegions(event.getOldWorld()).forEach(ITrackedRegion::unTrack);

        final Location location = event.getArena().getDeathMatch().getArenaSpawn();
        for (final INorthPlayer player : event.getArena().getPlayersManager().getAllPlayers())
        {
            player.sendMessage(this.messages, "separator");
            player.sendMessage(this.messages, "deathmatch.welcome", MessageLayout.CENTER);
            player.sendMessage(this.messages, "separator");
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
