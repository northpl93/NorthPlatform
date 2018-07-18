package pl.north93.zgame.antycheat.timeline.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/*default*/ class TickController
{
    private final Logger logger = LoggerFactory.getLogger(TickController.class);
    private final List<TickHandler> handlers = new ArrayList<>();
    @Inject
    private BukkitApiCore bukkitApiCore;

    @Bean
    private TickController()
    {
        // rejestrujemy ITickable i taska bukkitowego
        this.setup();
    }

    public void addTickHandler(final TickHandler tickHandler)
    {
        this.handlers.add(tickHandler);
    }

    private void setup()
    {
        MinecraftServer.getServer().a(this::fireEnd);
        Bukkit.getScheduler().runTaskTimer(this.bukkitApiCore.getPluginMain(), this::fireBegin, 0, 1); // delay 0, every tick
    }

    private void fireBegin()
    {
        for (final TickHandler handler : this.handlers)
        {
            handler.tickBegin();
        }
    }

    private void fireEnd()
    {
        for (final TickHandler handler : this.handlers)
        {
            try
            {
                handler.tickEnd();
            }
            catch (final Exception e)
            {
                this.logger.error("Exception occurred in post-tick code. Prevented server crash.", e);
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("handlers", this.handlers).toString();
    }
}

interface TickHandler
{
    void tickBegin();

    void tickEnd();
}