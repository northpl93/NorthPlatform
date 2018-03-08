package pl.arieals.lobby.npc;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.arieals.lobby.npc.xml.WorldNpcs;
import pl.arieals.lobby.npc.xml.XmlNpc;
import pl.north93.zgame.api.bukkit.server.IWorldInitializer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class NpcLoader implements IWorldInitializer
{
    private final NPCRegistry registry = CitizensAPI.createNamedNPCRegistry("hub", new MemoryNPCDataStore());
    @Inject
    private Logger logger;

    @Override
    public void initialiseWorld(final World world, final File directory)
    {
        this.loadNpcsInWorld(world, directory);
    }

    private void loadNpcsInWorld(final World world, final File directory)
    {
        final File xmlFile = new File(directory, "Hub.NPCs.xml");
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
