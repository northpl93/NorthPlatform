package pl.arieals.lobby.game.goldhunter;

import org.bukkit.event.EventHandler;

import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.lobby.game.HubListener;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class GoldHunterHubListener extends HubListener
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

        this.scoreboardManager.setLayout(event.getPlayer(), new GoldHunterHubScoreboard());
    }

    @Override
    public boolean isMyHub(final HubWorld hubWorld)
    {
        // Nazwa z pliku hubs.xml
        return hubWorld.getHubId().equals("goldHunter");
    }
}
