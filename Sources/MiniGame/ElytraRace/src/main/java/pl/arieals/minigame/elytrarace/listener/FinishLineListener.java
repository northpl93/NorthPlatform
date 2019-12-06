package pl.arieals.minigame.elytrarace.listener;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.region.IRegionManager;
import pl.north93.northplatform.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.api.bukkit.utils.region.Cuboid;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlCuboid;

public class FinishLineListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH) // post ArenaStartListener
    public void startGame(final GameStartEvent event)
    {
        final ElytraRaceArena arenaData = event.getArena().getArenaData();
        final XmlCuboid metaRegion = arenaData.getArenaConfig().getMetaRegion();
        final Cuboid metaCuboid = metaRegion.toCuboid(event.getArena().getWorld().getCurrentWorld());

        final IRegionManager regionManager = event.getArena().getRegionManager();
        final ITrackedRegion trackedRegion = regionManager.create(metaCuboid);

        trackedRegion.whenEnter(player ->
        {
            final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
            if (playerData.isFinished())
            {
                // gracz juz przekroczyl linie mety, wiecej razy nie mozemy go obslugiwac
                return;
            }

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2, false, false));

            // usuwamy czapke/glowe i elytre
            final PlayerInventory inventory = player.getInventory();
            inventory.setHelmet(null);
            inventory.setChestplate(null);

            arenaData.getMetaHandler().handle(event.getArena(), player, playerData);
        });
    }
}
