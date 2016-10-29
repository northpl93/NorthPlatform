package pl.north93.robbermod;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import pl.north93.robbermod.data.IRobberData;
import pl.north93.robbermod.data.RobberDataFactory;
import pl.north93.robbermod.data.RobberStorage;

@Mod(modid = "robbermod", useMetadata = true, serverSideOnly = true, acceptableRemoteVersions = "*")
public class RobberMod
{
    private static RobberMod instance;
    private Configuration config;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event)
    {
        instance = this;
        this.config = new Configuration(new File(event.getModConfigurationDirectory(), "pl.north93.robbermod.cfg"));
        CapabilityManager.INSTANCE.register(IRobberData.class, new RobberStorage(), new RobberDataFactory());
        MinecraftForge.EVENT_BUS.register(new StealListener());
        System.out.println("Robber mod started.");
    }

    @Mod.EventHandler
    public void onStop(final FMLServerStoppingEvent event)
    {
        this.config.save();
        System.out.println("Robber mod saved a config and stopped.");
    }

    public Configuration getConfig()
    {
        return this.config;
    }

    public static RobberMod getInstance()
    {
        return instance;
    }
}
