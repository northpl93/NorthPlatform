package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.arieals.api.minigame.shared.api.GameMap;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class ArenaInitListener implements Listener
{
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    @EventHandler(priority = EventPriority.LOW) // before normal
    public void onArenaInit(final GameInitEvent event)
    {
        final GameHostManager hostManager = this.server.getServerManager();
        final LocalArena arena = event.getArena();

        if (hostManager.getMiniGame().isDynamic())
        {
            // przenosimy wszystkich graczy do lobby areny jesli gra jest dynamiczna
            for (final Player player : arena.getPlayersManager().getPlayers())
            {
                hostManager.getLobbyManager().addPlayer(arena, player);
            }
        }
        else
        {
            arena.getPlayersManager().getPlayers().forEach(player -> player.kickPlayer("Powinienes wyleciec do poczekalni serwera, ale // TODO"));
            Bukkit.broadcastMessage("Now kick all players to server lobby");
            // todo kick all players to server lobby
        }

        if (hostManager.getMiniGame().getLobbyMode() == LobbyMode.INTEGRATED)
        {
            // jesli lobby jest zintegrowane z mapa to glosowanie na pewno jest wylaczone
            // i musimy juz teraz zaladowac nowa losowa mape.
            // Gracze nie beda mogli wejsc dopoki mapa sie nie zaladuje.
            final GameMap map = DioriteRandomUtils.getRandom(hostManager.getMiniGame().getGameMaps());
            arena.getWorld().setActiveMap(map);
        }
    }
}
