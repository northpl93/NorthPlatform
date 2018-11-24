package pl.north93.northplatform.antycheat.timeline.impl;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

/*default*/ class TickManager
{
    private TickImpl currentTick;

    @Bean
    private TickManager()
    {
    }

    public TickImpl getCurrentTick()
    {
        final int currentIdFromServer = this.getCurrentIdFromServer();
        if (this.currentTick == null || this.currentTick.getTickId() != currentIdFromServer)
        {
            return this.currentTick = new TickImpl(currentIdFromServer);
        }
        return this.currentTick;
    }

    public TickImpl getTick(final int tickId)
    {
        Preconditions.checkState(tickId > 0, "Tick ID is always grater than 0.");
        Preconditions.checkState(this.getCurrentIdFromServer() > tickId, "This method can only handle past ticks.");
        return new TickImpl(tickId, true);
    }

    private int getCurrentIdFromServer()
    {
        return MinecraftServer.currentTick;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currentTick", this.currentTick).toString();
    }
}
