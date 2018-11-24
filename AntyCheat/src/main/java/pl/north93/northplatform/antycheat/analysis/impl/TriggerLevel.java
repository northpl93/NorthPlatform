package pl.north93.northplatform.antycheat.analysis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.antycheat.analysis.reaction.ITriggerLevel;
import pl.north93.northplatform.antycheat.analysis.reaction.ITriggerListener;
import pl.north93.northplatform.antycheat.analysis.reaction.TriggerCheckContext;
import pl.north93.northplatform.antycheat.analysis.reaction.TriggerCondition;

/*default*/ class TriggerLevel implements ITriggerLevel
{
    private final TriggerCondition       condition;
    private final List<ITriggerListener> listeners;

    public TriggerLevel(final TriggerCondition condition)
    {
        this.condition = condition;
        this.listeners = new ArrayList<>(2);
    }

    public boolean checkCondition(final TriggerCheckContext context)
    {
        return this.condition.isTriggered(context);
    }

    @Override
    public void registerListener(final ITriggerListener listener)
    {
        this.listeners.add(listener);
    }

    public void fireListeners(final Player player, final boolean triggered)
    {
        final Consumer<ITriggerListener> invoker;
        if (triggered)
        {
            invoker = listener -> listener.onTriggered(player);
        }
        else
        {
            invoker = listener -> listener.onUnTriggered(player);
        }

        this.listeners.forEach(invoker);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("condition", this.condition).append("listeners", this.listeners).toString();
    }
}
