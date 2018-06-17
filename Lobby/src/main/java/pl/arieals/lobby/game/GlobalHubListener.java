package pl.arieals.lobby.game;

import org.bukkit.event.EventHandler;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.economy.impl.server.event.PlayerCurrencyChangedEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/**
 * Listenery powiązane z systemem Hubów, ale nie z konkretnym Hubem.
 */
public class GlobalHubListener implements AutoListener
{
    @Inject
    private IScoreboardManager scoreboardManager;

    @EventHandler
    public void updateScoreboardOnCurrencyChange(final PlayerCurrencyChangedEvent event)
    {
        final IScoreboardContext context = this.scoreboardManager.getContext(event.getPlayer());

        // scoreboardy zwykle mają w sobie zapisany stan waluty
        context.update();
    }
}
