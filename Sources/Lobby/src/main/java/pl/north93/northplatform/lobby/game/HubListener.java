package pl.north93.northplatform.lobby.game;

import org.bukkit.World;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.auth.api.IAuthManager;

/**
 * Klasa abstrakcyjna która po rozszerzeniu zostanie automatycznie
 * zarejestrowana jako listener Bukkita.
 *
 * Kazdy ten listener obsluguje jeden hub minigry. Metoda
 * isMyHub sprawdza czy event wykonuje sie na obslugiwanym
 * przez ten listener hubie.
 */
public abstract class HubListener implements AutoListener
{
    @Inject
    protected MiniGameServer miniGameServer;
    @Inject
    protected IAuthManager   authManager;

    /**
     * Sprawdza czy ten listener obsluguje danego huba.
     * Kazda klasa listenera powinna obslugiwac jeden hub.
     *
     * @param hubWorld Instancja reprezentujaca lokalny hub.
     * @return Czy ten listener obsluguje danego huba.
     */
    public abstract boolean isMyHub(HubWorld hubWorld);

    protected final HubWorld getHubWorld(final World world)
    {
        final LobbyManager serverManager = this.miniGameServer.getServerManager();
        return serverManager.getLocalHub().getHubWorld(world);
    }

    protected final boolean isLoggedIn(final Player player)
    {
        return this.authManager.isLoggedIn(player.getName());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
