package pl.north93.northplatform.lobby.game.elytra;

import org.bukkit.event.EventHandler;

import pl.north93.northplatform.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.lobby.game.HubListener;
import pl.north93.northplatform.lobby.ui.HubHotbar;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class ElytraHubListeners extends HubListener
{
    @Inject
    private IScoreboardManager scoreboardManager;

    @EventHandler
    public void playerJoinHub(final PlayerSwitchedHubEvent event)
    {
        if (! this.isMyHub(event.getNewHub()))
        {
            return;
        }

        this.scoreboardManager.setLayout(event.getPlayer(), new ElytraHubScoreboard());

        new HubHotbar().display(event.getPlayer());
    }

    @Override
    public boolean isMyHub(final HubWorld hubWorld)
    {
        return hubWorld.getHubId().equals("elytra");
    }
}
