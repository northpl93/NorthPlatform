package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;

public class WorldListener implements Listener
{
    @EventHandler
    public void onWorldInit(final WorldInitEvent event)
    {
        // nie marnujmy niepotrzebnie ramu
        event.getWorld().setKeepSpawnInMemory(false);
    }

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event)
    {
        // nie zapisujemy zmian. I tak sa zbedne
        event.setSaveChunk(false);
    }
}
