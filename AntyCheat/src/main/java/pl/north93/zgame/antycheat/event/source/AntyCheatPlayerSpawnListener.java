package pl.north93.zgame.antycheat.event.source;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import pl.north93.zgame.antycheat.event.impl.PlayerSpawnTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.PlayerSpawnTimelineEvent.SpawnReason;
import pl.north93.zgame.antycheat.timeline.impl.TimelineManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class AntyCheatPlayerSpawnListener implements AutoListener
{
    @Inject
    private TimelineManager timelineManager;

    @EventHandler
    public void onInitialSpawn(final PlayerInitialSpawnEvent event)
    {
        final Player player = event.getPlayer();
        final PlayerSpawnTimelineEvent timelineEvent = new PlayerSpawnTimelineEvent(player, SpawnReason.SERVER_JOIN);

        this.timelineManager.pushEventForPlayer(player, timelineEvent);
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event)
    {
        final Player player = event.getPlayer();
        final SpawnReason spawnReason = event.isBedSpawn() ? SpawnReason.BED : SpawnReason.RESPAWN_SCREEN;
        final PlayerSpawnTimelineEvent timelineEvent = new PlayerSpawnTimelineEvent(player, spawnReason);

        this.timelineManager.pushEventForPlayer(player, timelineEvent);
    }
}
