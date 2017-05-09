package pl.arieals.api.minigame.server.gamehost.listener;

import static org.diorite.utils.math.DioriteRandomUtils.getRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.MapVote;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.utils.Timer;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.MapTemplate;
import pl.arieals.api.minigame.shared.api.MiniGameConfig;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class GameStartScheduler implements Listener
{
    private static final int GAME_START_COOLDOWN = 30;
    private BukkitApiCore  apiCore;
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;
    @InjectMessages("MiniGameApi")
    private MessagesBox    messages;

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

        final MiniGameConfig miniGame = gameHostManager.getMiniGameConfig();
        if (miniGame.getMapVoting().getEnabled())
        {
            // odpalamy glosowanie
            final List<MapTemplate> maps = new ArrayList<>();
            getRandom(gameHostManager.getMapTemplateManager().getAllTemplates(), maps, miniGame.getMapVoting().getNumberOfMaps(), true);

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
        arena.getPlayersManager().broadcast(this.messages, "vote.started");

        for (int i = 0; i < mapVote.getOptions().length; i++)
        {
            final MapTemplate gameMap = mapVote.getOptions()[i];
            arena.getPlayersManager().broadcast(this.messages, "vote.option_line", i, gameMap.getDisplayName());
        }
    }

    private void completeVoting(final LocalArena arena)
    {
        final MapVote mapVote = arena.getWorld().getMapVote();

        final MapTemplate winner = mapVote.getWinner();
        arena.getPlayersManager().broadcast(this.messages, "vote.winner", winner.getDisplayName());

        arena.getWorld().setActiveMap(winner);
        mapVote.resetVoting();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
