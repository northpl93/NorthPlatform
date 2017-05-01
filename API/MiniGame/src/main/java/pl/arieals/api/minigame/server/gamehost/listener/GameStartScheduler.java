package pl.arieals.api.minigame.server.gamehost.listener;

import static org.diorite.utils.math.DioriteRandomUtils.getRandom;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.MapVote;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.utils.Timer;
import pl.arieals.api.minigame.shared.api.GameMap;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.MiniGame;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class GameStartScheduler implements Listener
{
    private static final int GAME_START_COOLDOWN = 30;
    private BukkitApiCore  apiCore;
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    @EventHandler
    public void onArenaJoin(final PlayerJoinArenaEvent event)
    {
        final GameHostManager gameHostManager = this.server.getServerManager();
        final LocalArena arena = event.getArena();
        final Timer timer = arena.getTimer();
        if (timer.isStarted() || arena.getGamePhase() != GamePhase.LOBBY)
        {
            // licznik wystartowal lub arena nie jest w lobby
            return;
        }
        if (! arena.getPlayersManager().isEnoughToStart())
        {
            // brakuje graczy
            return;
        }

        timer.start(GAME_START_COOLDOWN, TimeUnit.SECONDS, false);
        Bukkit.getScheduler().runTaskLater(
                this.apiCore.getPluginMain(),
                () -> this.startArena(arena),
                timer.calcTimeToInTicks(0, TimeUnit.MILLISECONDS));

        final MiniGame miniGame = gameHostManager.getMiniGame();
        if (miniGame.getMapVoting().getEnabled())
        {
            // odpalamy glosowanie
            final List<GameMap> maps = new ArrayList<>();
            getRandom(miniGame.getGameMaps(), maps, miniGame.getMapVoting().getNumberOfMaps(), true);

            final MapVote mapVote = arena.getWorld().getMapVote();
            mapVote.startVote(maps);
            this.printStartVoteInfo(arena);

            Bukkit.getScheduler().runTaskLater(
                    this.apiCore.getPluginMain(),
                    () -> this.completeVoting(arena),
                    timer.calcTimeToInTicks(7, TimeUnit.SECONDS));
        }
    }

    private void startArena(final LocalArena arena)
    {
        if (! arena.getPlayersManager().isEnoughToStart())
        {
            arena.getTimer().stop();
            return;
        }
        arena.setGamePhase(GamePhase.STARTED);
    }

    private void printStartVoteInfo(final LocalArena arena)
    {
        final MapVote mapVote = arena.getWorld().getMapVote();
        Bukkit.broadcastMessage("Rozpoczeto glosowanie na mape na arenie " + arena.getId());

        for (int i = 0; i < mapVote.getOptions().length; i++)
        {
            final GameMap gameMap = mapVote.getOptions()[i];
            Bukkit.broadcastMessage(MessageFormat.format("[{0}] {1}", i, gameMap.getDisplayName()));
        }
    }

    private void completeVoting(final LocalArena arena)
    {
        final MapVote mapVote = arena.getWorld().getMapVote();
        Bukkit.broadcastMessage("Zakonczono glosowanie na mape na arenie " + arena.getId());

        final GameMap winner = mapVote.getWinner();
        Bukkit.broadcastMessage("Wygrala mapa: " + winner.getDisplayName());

        arena.getWorld().setActiveMap(winner);
        mapVote.resetVoting();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
