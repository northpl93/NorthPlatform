package pl.north93.zgame.api.bukkit.server.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.server.IWorldInitializer;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

/*default*/ class WorldInitializationHandler implements Listener
{
    private final Logger                  logger;
    private final List<IWorldInitializer> initializers = new ArrayList<>();

    @Bean
    private WorldInitializationHandler(final BukkitApiCore apiCore, final Logger logger)
    {
        apiCore.registerEvents(this);
        this.logger = logger;
    }

    @Aggregator(IWorldInitializer.class)
    public void addInitializer(final IWorldInitializer initializer)
    {
        final String initializerName = initializer.getClass().getSimpleName();
        this.logger.log(Level.INFO, "Registering new world initializer {0}", initializerName);

        this.initializers.add(initializer);
        Bukkit.getWorlds().forEach(world -> this.callInitializer(initializer, world));
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event)
    {
        final World world = event.getWorld();
        this.initializers.forEach(initializer -> this.callInitializer(initializer, world));
    }

    private void callInitializer(final IWorldInitializer initializer, final World world)
    {
        final String initializerName = initializer.getClass().getSimpleName();
        this.logger.log(Level.INFO, "Calling initializer {0} for world {1}", new Object[]{initializerName, world.getName()});

        final File worldDir = new File(Bukkit.getWorldContainer(), world.getName());
        initializer.initialiseWorld(world, worldDir);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("initializers", this.initializers).toString();
    }
}
