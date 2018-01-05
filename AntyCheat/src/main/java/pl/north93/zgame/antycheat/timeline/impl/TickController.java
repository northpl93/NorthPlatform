package pl.north93.zgame.antycheat.timeline.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.v1_10_R1.MinecraftServer;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/*default*/ class TickController
{
    @Inject
    private BukkitApiCore bukkitApiCore;
    private final List<TickHandler> handlers = new ArrayList<>();

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
                this.bukkitApiCore.getLogger().log(Level.SEVERE, "Exception occurred in post-tick code. Prevented server crash.", e);
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