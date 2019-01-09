package pl.north93.northplatform.antycheat.event.source;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import pl.north93.northplatform.antycheat.event.impl.PlayerSpawnTimelineEvent;
import pl.north93.northplatform.antycheat.event.impl.PlayerSpawnTimelineEvent.SpawnReason;
import pl.north93.northplatform.antycheat.event.impl.TeleportTimelineEvent;
import pl.north93.northplatform.antycheat.event.impl.VelocityAppliedTimelineEvent;
import pl.north93.northplatform.antycheat.timeline.impl.TimelineManager;
import pl.north93.northplatform.antycheat.utils.location.RichEntityLocation;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class AntyCheatBukkitListener implements AutoListener
{
    @Inject
    private TimelineManager timelineManager;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
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

    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event)
    {
        final Player player = event.getPlayer();

        final RichEntityLocation from = new RichEntityLocation(player, event.getFrom());
        final RichEntityLocation to = new RichEntityLocation(player, event.getTo());

        final TeleportTimelineEvent timelineEvent = new TeleportTimelineEvent(player, from, to);

        this.timelineManager.pushEventForPlayer(player, timelineEvent);
    }

    @EventHandler
    public void onVelocityChange(final PlayerVelocityEvent event)
    {
        final Player player = event.getPlayer();
        final VelocityAppliedTimelineEvent timelineEvent = new VelocityAppliedTimelineEvent(player, event.getVelocity());

        this.timelineManager.pushEventForPlayer(player, timelineEvent);
    }
}
