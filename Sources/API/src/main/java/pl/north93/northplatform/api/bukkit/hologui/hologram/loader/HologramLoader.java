package pl.north93.northplatform.api.bukkit.hologui.hologram.loader;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologram;
import pl.north93.northplatform.api.bukkit.hologui.hologram.impl.HologramFactory;
import pl.north93.northplatform.api.bukkit.server.IWorldInitializer;
import pl.north93.northplatform.api.global.utils.JaxbUtils;

public class HologramLoader implements IWorldInitializer
{
    @Override
    public void initialiseWorld(final World world, final File directory)
    {
        final File xmlFile = new File(directory, "Holograms.xml");
        if (! xmlFile.exists())
        {
            return;
        }

        final HologramsConfig holograms = JaxbUtils.unmarshal(xmlFile, HologramsConfig.class);
        for (final HologramEntryConfig entryConfig : holograms.getHolograms())
        {
            final Location location = entryConfig.getLocation().toBukkit(world);

            final IHologram hologram = HologramFactory.create(location);
            hologram.setMessage(entryConfig);
        }
    }
}
