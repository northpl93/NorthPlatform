package pl.north93.northplatform.lobby.game.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.north93.northplatform.lobby.game.HubListener;
import pl.north93.northplatform.lobby.ui.HubHotbar;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.auth.server.event.PlayerSuccessfullyAuthEvent;

public class MainHubListener extends HubListener
{
    @Inject
    private IScoreboardManager scoreboardManager;

    @EventHandler(priority = EventPriority.HIGH)
    public void playerJoinHub(final PlayerSwitchedHubEvent event)
    {
        if (! this.isMyHub(event.getNewHub()))
        {
            return;
        }

        final Player player = event.getPlayer();
        if (this.isLoggedIn(player))
        {
            // jesli gracz jest premium to od razu pokazujemy mu interfejs uzytkownika
            this.doShowGui(player);
        }
    }

    @EventHandler
    public void showGuiWhenNoPremiumPlayerLogIn(final PlayerSuccessfullyAuthEvent event)
    {
        // ten event wywolywany jest tylko dla logujacych sie graczy no-premium
        this.doShowGui(event.getPlayer());
    }

    private void doShowGui(final Player player)
    {
        this.scoreboardManager.setLayout(player, new MainHubScoreboard());

        // ta metoda pierwszy raz po wlaczeniu serwera zajmuje duza ilosc czasu
        // bo wywola leniwe wczytywanie plików *.xml z GUI i hotbarami.
        new HubHotbar().display(player);
    }

    @Override
    public boolean isMyHub(final HubWorld hubWorld)
    {
        return hubWorld.getHubId().equals("main");
    }
}
