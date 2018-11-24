package pl.north93.northplatform.antycheat.client.monitor;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.antycheat.analysis.reaction.DefaultViolationMapper;
import pl.north93.northplatform.antycheat.analysis.reaction.ITriggerListener;
import pl.north93.northplatform.antycheat.analysis.reaction.IViolationMonitor;
import pl.north93.northplatform.antycheat.analysis.reaction.MappedPointsCondition;
import pl.north93.northplatform.antycheat.client.monitor.action.IAntyCheatAction;

public final class WrappedViolationMonitor
{
    private final IViolationMonitor monitor;

    public WrappedViolationMonitor(final IViolationMonitor monitor)
    {
        this.monitor = monitor;
    }

    public void addAction(final int points, final IAntyCheatAction action)
    {
        this.monitor.addLevel(new MappedPointsCondition(new DefaultViolationMapper(), points), new ITriggerListener()
        {
            @Override
            public void onTriggered(final Player player)
            {
                action.handle(player, WrappedViolationMonitor.this.monitor.getViolation());
            }

            @Override
            public void onUnTriggered(final Player player)
            {
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("monitor", this.monitor).toString();
    }
}
