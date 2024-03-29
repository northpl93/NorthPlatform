package pl.north93.northplatform.antycheat.timeline.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@Slf4j
/*default*/ class TickController
{
    private final List<TickHandler> handlers = new ArrayList<>();
    @Inject
    private IBukkitExecutor bukkitExecutor;

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
        this.bukkitExecutor.syncTimer(1, this::fireBegin);
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
                log.error("Exception occurred in post-tick code. Prevented server crash.", e);
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