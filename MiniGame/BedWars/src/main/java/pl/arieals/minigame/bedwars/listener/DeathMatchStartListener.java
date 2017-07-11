package pl.arieals.minigame.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchLoadedEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;

public class DeathMatchStartListener implements Listener
{
    @EventHandler
    public void onDeathMatchStart(final DeathMatchLoadedEvent event)
    {
        final LocalArena arena = event.getArena();
        Bukkit.broadcastMessage("Bedwars is preparing deathmatch on " + arena.getId());

        final BedWarsArena arenaData = arena.getArenaData();
        arenaData.getGenerators().clear(); // usuwamy generatory by je wylaczyc
        arenaData.getPlayerBlocks().clear(); // usuwamy bloki by nie trzymac referencji na swiat
        arenaData.getSecureRegions().clear(); // usuwamy bo niepotrzebne
    }
}
