package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartedEvent;
import pl.arieals.api.minigame.server.gamehost.region.IRegionManager;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.elytrarace.ElytraRaceMode;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;

public class MetaListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH) // post ArenaStartListener
    public void startGame(final GameStartedEvent event)
    {
        final ElytraRaceArena arenaData = event.getArena().getArenaData();
        final XmlCuboid metaRegion = arenaData.getArenaConfig().getMetaRegion();
        final Cuboid metaCuboid = metaRegion.toCuboid(event.getArena().getWorld().getCurrentWorld());

        final IRegionManager regionManager = event.getArena().getRegionManager();
        final ITrackedRegion trackedRegion = regionManager.create(metaCuboid);

        trackedRegion.whenEnter(player -> this.handleMeta(player, event.getArena()));
    }

    private void handleMeta(final Player player, final LocalArena arena)
    {
        final ElytraRaceArena arenaData = arena.getArenaData();
        if (arenaData.getGameMode() == ElytraRaceMode.RACE_MODE)
        {
            final int playerPlace = arenaData.getPlace() + 1;
            arenaData.setPlace(playerPlace);

            player.sendMessage("Zajales " + playerPlace + " miejsce!");
        }
        else
        {
            final ElytraScorePlayer scorePlayer = getPlayerData(player, ElytraScorePlayer.class);
            final int points = scorePlayer.getPoints();

            arenaData.getPoints().put(player.getUniqueId(), points);

            // todo
        }
    }
}
