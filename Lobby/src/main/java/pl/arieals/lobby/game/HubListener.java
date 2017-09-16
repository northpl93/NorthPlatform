package pl.arieals.lobby.game;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

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
    protected BukkitApiCore  apiCore;
    @Inject
    protected MiniGameServer miniGameServer;

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
