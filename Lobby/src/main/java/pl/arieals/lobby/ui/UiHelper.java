package pl.arieals.lobby.ui;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.lobby.hub.visibility.DefaultHubVisibilityPolicy;
import pl.arieals.api.minigame.server.lobby.hub.visibility.HubVisibilityService;
import pl.arieals.api.minigame.server.lobby.hub.visibility.IHubVisibilityPolicy;
import pl.arieals.api.minigame.server.lobby.hub.visibility.NobodyHubVisibilityPolicy;
import pl.arieals.api.minigame.server.lobby.hub.visibility.PartyHubVisibilityPolicy;
import pl.arieals.lobby.play.PlayGameController;
import pl.north93.zgame.api.bukkit.gui.element.dynamic.DynamicElementData;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.uri.UriHandler;
import pl.north93.zgame.api.global.uri.UriInvocationContext;
import pl.north93.zgame.api.global.utils.Vars;

public final class UiHelper
{
    @Inject
    private INetworkManager      networkManager;
    @Inject
    private PlayGameController   playController;
    @Inject
    private HubVisibilityService hubVisibilityService;

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

    @UriHandler("/lobby/ui/instancePicker/instances")
    public Collection<DynamicElementData> listHubInstances(final UriInvocationContext context)
    {
        return Arrays.asList(new DynamicElementData(Vars.empty(), (p1, p2) -> Bukkit.broadcastMessage("test")));
    }

    @UriHandler("/lobby/ui/visibility/:mode/:playerId")
    public void switchVisibility(final UriInvocationContext context)
    {
        final INorthPlayer player = INorthPlayer.get(context.asUuid("playerId"));

        final IHubVisibilityPolicy policy;
        switch (context.asString("mode"))
        {
            case "nobody":
                policy = NobodyHubVisibilityPolicy.INSTANCE;
                break;
            case "everyone":
                policy = DefaultHubVisibilityPolicy.INSTANCE;
                break;
            case "party":
                policy = PartyHubVisibilityPolicy.INSTANCE;
                break;
            default:
                throw new IllegalArgumentException();
        }

        this.hubVisibilityService.setPolicy(player, policy);
    }
}
