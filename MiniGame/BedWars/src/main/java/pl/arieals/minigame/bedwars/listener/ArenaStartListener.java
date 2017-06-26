package pl.arieals.minigame.bedwars.listener;

import javax.xml.bind.JAXB;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.cfg.BedWarsArenaConfig;

public class ArenaStartListener implements Listener
{
    @EventHandler
    public void onLobbyInit(final LobbyInitEvent event)
    {
        final LocalArena arena = event.getArena();

        final BedWarsArenaConfig config = JAXB.unmarshal(arena.getWorld().getResource("BedWarsArena.xml"), BedWarsArenaConfig.class);
        arena.setArenaData(new BedWarsArena(arena, config));
    }
}
