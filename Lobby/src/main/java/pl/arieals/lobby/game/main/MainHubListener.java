package pl.arieals.lobby.game.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import pl.arieals.api.minigame.server.lobby.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.lobby.game.HubListener;
import pl.arieals.lobby.ui.HubHotbar;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class MainHubListener extends HubListener
{
    @Inject
    private IScoreboardManager scoreboardManager;
    @Inject
    private IBukkitExecutor    bukkitExecutor;

    @EventHandler(priority = EventPriority.HIGH)
    public void playerJoinHub(final PlayerSwitchedHubEvent event)
    {
        if (! this.isMyHub(event.getNewHub()))
        {
            return;
        }

        this.scoreboardManager.setLayout(event.getPlayer(), new MainHubScoreboard());

        // Chyba wystarczy ze zrobimy to tylko tu?
        new HubHotbar().display(event.getPlayer());
    }

    @Override
    public boolean isMyHub(final HubWorld hubWorld)
    {
        return hubWorld.getHubId().equals("main");
    }
}
