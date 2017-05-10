package pl.north93.zgame.api.bukkit.scoreboard.impl;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.global.component.Component;

public class ScoreboardManagerImpl extends Component implements IScoreboardManager
{
    private BukkitApiCore apiCore;

    @Override
    public IScoreboardContext setLayout(final Player player, final IScoreboardLayout layout)
    {
        final ScoreboardContextImpl context = new ScoreboardContextImpl(player, layout);
        this.setContext(player, context);
        return context;
    }

    @Override
    public IScoreboardContext getContext(final Player player)
    {
        return (IScoreboardContext) player.getMetadata("scoreboard_context").get(0).value();
    }

    private void setContext(final Player player, final ScoreboardContextImpl scoreboardContext)
    {
        player.setMetadata("scoreboard_context", new FixedMetadataValue(this.apiCore.getPluginMain(), scoreboardContext));
    }

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }
}
