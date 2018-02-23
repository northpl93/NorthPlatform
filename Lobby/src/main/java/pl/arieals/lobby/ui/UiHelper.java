package pl.arieals.lobby.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.arieals.lobby.play.PlayGameController;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.uri.UriHandler;
import pl.north93.zgame.api.global.uri.UriInvocationContext;

public final class UiHelper
{
    @Inject
    private INetworkManager    networkManager;
    @Inject
    private PlayGameController playController;

    @UriHandler("/lobby/ui/playersCount")
    public int getPlayersCount(final UriInvocationContext context)
    {
        return this.networkManager.getProxies().onlinePlayersCount();
    }

    @UriHandler("/lobby/ui/switchHub/:hubId/:playerId")
    public void switchPlayerHub(final UriInvocationContext context)
    {
        final Player player = Bukkit.getPlayer(context.asUuid("playerId"));
        final String hubId = context.asString("hubId");

        this.playController.switchHub(player, hubId);
    }
}
