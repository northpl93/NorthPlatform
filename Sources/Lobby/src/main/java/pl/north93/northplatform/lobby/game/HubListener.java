package pl.north93.northplatform.lobby.game;

import org.bukkit.World;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.auth.api.IAuthManager;

/**
 * An abstract class holding boilerplate for all hub listeners.
 * It will be automatically registered as Bukkit listener.
 *
 * Every listener handles one hub.
 * isMyHub() checks do hub is supported by this listener
 */
public abstract class HubListener implements AutoListener
{
    @Inject
    protected LobbyManager lobbyManager;
    @Inject
    protected IAuthManager authManager;

    /**
     * Checks do this listener handles this hub.
     * Every listener class should handle only one hub.
     *
     * @param hubWorld An instance representing a local hub.
     * @return Does this listener support the hub from the argument.
     */
    public abstract boolean isMyHub(HubWorld hubWorld);

    protected final HubWorld getHubWorld(final World world)
    {
        return this.lobbyManager.getLocalHub().getHubWorld(world);
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
