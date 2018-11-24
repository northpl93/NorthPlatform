package pl.north93.groovyscript.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.groovyscript.api.IScriptContext;
import pl.north93.groovyscript.api.source.DirectoryScriptSource;
import pl.north93.northplatform.api.bukkit.server.event.ServerStartedEvent;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class BukkitContextManager implements AutoListener
{
    private final Map<World, IScriptContext> worldContext = new HashMap<>();
    @Inject
    private GroovyManagerImpl groovyManager;

    private BukkitContextManager()
    {
    }

    @EventHandler
    public void onServerFullyStarted(final ServerStartedEvent event)
    {
        Bukkit.getWorlds().forEach(this::loadScriptsForWorld);

        final File serverScripts = new File("scripts");
        if (! serverScripts.exists() || ! serverScripts.isDirectory())
        {
            return;
        }

        this.groovyManager.createContext(new DirectoryScriptSource(serverScripts));
    }

    @EventHandler
    public void createWorldContextOnLoad(final WorldLoadEvent event)
    {
        if (this.isServerStarted())
        {
            this.loadScriptsForWorld(event.getWorld());
        }
    }

    @EventHandler
    public void destroyWorldContextOnUnload(final WorldUnloadEvent event)
    {
        final IScriptContext context = this.worldContext.remove(event.getWorld());
        if (context == null)
        {
            return;
        }

        context.destroy();
    }

    private boolean isServerStarted()
    {
        return MinecraftServer.currentTick > 0;
    }

    private void loadScriptsForWorld(final World world)
    {
        final File worldFolder = world.getWorldFolder();

        final File scriptsFolder = new File(worldFolder, "scripts");
        if (! scriptsFolder.exists() || ! scriptsFolder.isDirectory())
        {
            return;
        }

        final IScriptContext context = this.groovyManager.createContext(new DirectoryScriptSource(scriptsFolder));
        this.worldContext.put(world, context);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("worldContext", this.worldContext).toString();
    }
}
