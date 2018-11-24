package pl.north93.northplatform.antycheat.analysis.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

/**
 * Reprezentuje status danego monitora (ilość punktów, aktywne poziomy) u danego gracza.
 */
/*default*/ class MonitorStatus
{
    private final Player               player;
    private final ViolationMonitorImpl monitor;
    private final Set<TriggerLevel>    triggered;

    public MonitorStatus(final Player player, final ViolationMonitorImpl monitor)
    {
        this.player = player;
        this.monitor = monitor;
        this.triggered = new HashSet<>(0);
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public ViolationMonitorImpl getMonitor()
    {
        return this.monitor;
    }

    public boolean isTriggered(final TriggerLevel triggerLevel)
    {
        return this.triggered.contains(triggerLevel);
    }

    public boolean setTriggered(final TriggerLevel triggerLevel, final boolean triggered)
    {
        if (triggered)
        {
            return this.triggered.add(triggerLevel);
        }
        return this.triggered.remove(triggerLevel);
    }
}
