package pl.north93.northplatform.api.bukkit.server.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.server.IWorldInitializer;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
public class WorldInitializationHandler implements Listener
{
    private final List<IWorldInitializer> initializers = new ArrayList<>();

    @Bean
    private WorldInitializationHandler(final IBukkitServerManager serverManager)
    {
        serverManager.registerEvents(this);
    }

    @Aggregator(IWorldInitializer.class)
    public void addInitializer(final IWorldInitializer initializer)
    {
        final String initializerName = initializer.getClass().getSimpleName();
        log.info("Registering new world initializer {}", initializerName);

        this.initializers.add(initializer);
        Bukkit.getWorlds().forEach(world -> this.callInitializer(initializer, world));
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event)
    {
        final World world = event.getWorld();
        this.callInitializers(world);
    }

    public void callInitializers(final World world)
    {
        this.initializers.forEach(initializer -> this.callInitializer(initializer, world));
    }

    private void callInitializer(final IWorldInitializer initializer, final World world)
    {
        final String initializerName = initializer.getClass().getSimpleName();

        log.info("Calling initializer {} for world {}", initializerName, world.getName());

        final File worldDir = new File(Bukkit.getWorldContainer(), world.getName());
        try
        {
            initializer.initialiseWorld(world, worldDir);
        }
        catch (final Exception e)
        {
            log.error("Initializer {} throw exception for world {}", initializerName, world.getName(), e);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("initializers", this.initializers).toString();
    }
}
