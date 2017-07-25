package pl.arieals.minigame.bedwars.listener;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.cfg.BwTeamConfig;

public class NpcCreator implements Listener
{
    @EventHandler
    public void createNpc(final GameStartEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();

        for (final BwTeamConfig bwTeamConfig : arenaData.getConfig().getTeams())
        {
            final Location location = bwTeamConfig.getUpgradesNpc().toBukkit(event.getArena().getWorld().getCurrentWorld());

            final NPCRegistry npcRegistry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
            final NPC testowy = npcRegistry.createNPC(EntityType.VILLAGER, "testowy");
            testowy.spawn(location);
        }
    }
}
