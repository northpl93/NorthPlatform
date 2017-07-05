package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.DeathMatchPrepareEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DeathMatchStartListener implements Listener
{
    @Inject
    private MiniGameServer server;

    @EventHandler
    public void onDeathMatchStart(final DeathMatchPrepareEvent event)
    {
        final GameHostManager serverManager = this.server.getServerManager();

        // usuwamy wszystkie sledzone regiony z starego swiata
        serverManager.getRegionManager().getRegions(event.getOldWorld()).forEach(ITrackedRegion::unTrack);
    }
}
