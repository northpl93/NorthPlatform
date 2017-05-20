package pl.north93.zgame.api.bukkit.scoreboard.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

class LayoutUpdateTask extends BukkitRunnable
{
    private final ScoreboardManagerImpl manager;
    private final IScoreboardLayout     layout;

    public LayoutUpdateTask(final ScoreboardManagerImpl manager, final IScoreboardLayout layout)
    {
        this.manager = manager;
        this.layout = layout;
    }

    @Override
    public void run()
    {
        boolean updated = false;

        for (final Player player : Bukkit.getOnlinePlayers())
        {
            final ScoreboardContextImpl context = this.manager.getContext(player);
            if (context.getLayout() != this.layout)
            {
                continue;
            }

            context.update();
            updated = true;
        }

        if (! updated)
        {
            this.cancel();
            this.manager.removeTask(this);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("layout", this.layout).toString();
    }
}
