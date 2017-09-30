package pl.north93.zgame.api.bukkit.scoreboard.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ScoreboardManagerImpl extends Component implements IScoreboardManager
{
    private final Map<LayoutUpdateTask, IScoreboardLayout> layoutUpdaters;
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private IBukkitExecutor bukkitExecutor;

    public ScoreboardManagerImpl()
    {
        this.layoutUpdaters = new HashMap<>();
    }

    @Override
    public IScoreboardContext setLayout(final Player player, final IScoreboardLayout layout)
    {
        final ScoreboardContextImpl context = new ScoreboardContextImpl(this.bukkitExecutor, player, layout);
        this.setContext(player, context);
        this.checkRunUpdater(layout);
        return context;
    }

    @Override
    public ScoreboardContextImpl getContext(final Player player)
    {
        final List<MetadataValue> metadata = player.getMetadata("scoreboard_context");
        if (metadata.isEmpty())
        {
            return null;
        }
        return (ScoreboardContextImpl) metadata.get(0).value();
    }

    @Override
    public void removeScoreboard(final Player player)
    {
        final ScoreboardContextImpl context = this.getContext(player);
        if (context == null)
        {
            return;
        }

        context.cleanup();
        player.removeMetadata("scoreboard_context", this.apiCore.getPluginMain());
    }

    private void setContext(final Player player, final ScoreboardContextImpl scoreboardContext)
    {
        final ScoreboardContextImpl old = this.getContext(player);
        if (old != null)
        {
            old.cleanup();
        }
        player.setMetadata("scoreboard_context", new FixedMetadataValue(this.apiCore.getPluginMain(), scoreboardContext));
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        scoreboardContext.update();
    }

    private void checkRunUpdater(final IScoreboardLayout layout)
    {
        final int updateEvery = layout.updateEvery();
        if (updateEvery <= 0)
        {
            return;
        }

        // uruchamiane taski sa synchroniczne, API jest przeznaczone
        // do uzytko synchronicznego wiec nie musimy sie bac, ze
        // cos sie spierdoli.
        if (this.layoutUpdaters.values().contains(layout))
        {
            return;
        }

        final LayoutUpdateTask task = new LayoutUpdateTask(this, layout);
        this.layoutUpdaters.put(task, layout);
        task.runTaskTimer(this.apiCore.getPluginMain(), updateEvery, updateEvery);
    }

    /*default*/ void removeTask(final LayoutUpdateTask task)
    {
        this.layoutUpdaters.remove(task);
    }

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
