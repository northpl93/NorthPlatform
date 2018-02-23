package pl.arieals.lobby.npc;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.arieals.lobby.npc.xml.WorldNpcs;
import pl.arieals.lobby.npc.xml.XmlNpc;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class NpcLoader implements Listener
{
    private final NPCRegistry registry = CitizensAPI.createNamedNPCRegistry("hub", new MemoryNPCDataStore());
    @Inject
    private Logger logger;

    @Bean
    private NpcLoader(final BukkitApiCore apiCore)
    {
        apiCore.registerEvents(this);
        try
        {
            Bukkit.getWorlds().forEach(this::loadNpcsInWorld);
        }
        catch (final Exception e)
        {
            this.logger.log(Level.SEVERE, "An exception has been thrown while loading NPCs", e);
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event)
    {
        final World world = event.getWorld();
        this.loadNpcsInWorld(world);
    }

    private void loadNpcsInWorld(final World world)
    {
        //new Exception().printStackTrace();
        final File worldDir = new File(Bukkit.getWorldContainer(), world.getName());

        final File xmlFile = new File(worldDir, "Hub.NPCs.xml");
        if (! xmlFile.exists())
        {
            return;
        }

        this.logger.log(Level.INFO, "Loading NPCs from {0}", xmlFile);

        final WorldNpcs npcs = JAXB.unmarshal(xmlFile, WorldNpcs.class);
        for (final XmlNpc xmlNpc : npcs.getNpcs())
        {
            xmlNpc.createNpc(this.registry, world);
        }
    }
}
