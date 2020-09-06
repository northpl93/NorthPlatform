package pl.north93.northplatform.lobby.ui;

import static pl.north93.northplatform.api.bukkit.gui.element.dynamic.DynamicElementData.builder;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.gui.element.dynamic.DynamicElementData;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.proxy.IProxiesManager;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;
import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.IArenaClient;
import pl.north93.northplatform.api.minigame.server.lobby.hub.LocalHubServer;
import pl.north93.northplatform.api.minigame.server.lobby.hub.visibility.DefaultHubVisibilityPolicy;
import pl.north93.northplatform.api.minigame.server.lobby.hub.visibility.HubVisibilityService;
import pl.north93.northplatform.api.minigame.server.lobby.hub.visibility.IHubVisibilityPolicy;
import pl.north93.northplatform.api.minigame.server.lobby.hub.visibility.NobodyHubVisibilityPolicy;
import pl.north93.northplatform.api.minigame.server.lobby.hub.visibility.PartyHubVisibilityPolicy;
import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;
import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;
import pl.north93.northplatform.lobby.play.PlayGameController;

public final class UiHelper
{
    @Inject
    private IProxiesManager proxiesManager;
    @Inject
    private PlayGameController playController;
    @Inject
    private HubVisibilityService hubVisibilityService;
    @Inject
    private IArenaClient arenaClient;

    @UriHandler("/lobby/ui/playersCount")
    public int getPlayersCount(final UriInvocationContext context)
    {
        return this.proxiesManager.onlinePlayersCount();
    }

    @UriHandler("/lobby/ui/inGamePlayersCount/:gameId")
    public int getInGamePlayersCount(final UriInvocationContext context)
    {
        final String gameId = context.asString("gameId");
        
        return this.arenaClient.getAll().stream().filter(arena ->
        {
            if (arena == null) // todo usunac to po jakims czasie jak bug nie bedzie juz wystepowal
            {
                System.err.println("arena is null!");
                return false;
            }
            else if (arena.getMiniGame() == null)
            {
                System.err.println("arena.getMiniGame is null in arena: " + arena);
                return false;
            }

            return arena.getMiniGame().getGameId().equals(gameId);
        }).mapToInt(IArena::getPlayersCount).sum();
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
        final List<DynamicElementData> elements = new ArrayList<>();

        final Comparator<IHubServer> comparator = Comparator.comparing(IHubServer::getServerId);
        final List<IHubServer> hubs = this.playController.getHubs().stream().sorted(comparator).collect(Collectors.toList());

        int counter = 0;
        for (final IHubServer hubServer : hubs)
        {
            final Vars<Object> vars = Vars.of("id", ++counter);
            elements.add(builder().vars(vars).iconCase(this.getIconCase(hubServer)).clickHandler((source, event) ->
            {
                this.playController.switchHubInstance(event.getWhoClicked(), hubServer);
            }).build());
        }

        return elements;
    }

    // zwraca wariant ikony do wyswietlenia w dynamicznym kontenerze z lista hubów
    private String getIconCase(final IHubServer hubServer)
    {
        final LocalHubServer thisHubServer = this.playController.getThisHubServer();
        if (thisHubServer.getServerId().equals(hubServer.getServerId()))
        {
            return "actual";
        }
        return "nonactual";
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
