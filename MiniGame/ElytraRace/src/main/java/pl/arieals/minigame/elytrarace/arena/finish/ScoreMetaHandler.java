package pl.arieals.minigame.elytrarace.arena.finish;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ScoreMetaHandler implements IFinishHandler
{
    @Inject
    @Messages("ElytraRace")
    private MessagesBox messages;
    private final Map<UUID, Integer> points = new HashMap<>(); // uzywane w SCORE_MODE do przyznawania nagrod nawet gdy gracz wyjdzie

    @Override
    public void handle(final LocalArena arena, final Player player)
    {
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        if (playerData.isFinished())
        {
            return;
        }
        playerData.setFinished(true);

        final ElytraScorePlayer scoreData = getPlayerData(player, ElytraScorePlayer.class);

        this.messages.sendMessage(player, "score.finish.your_points", scoreData.getPoints());
        this.points.put(player.getUniqueId(), scoreData.getPoints());

        if (! IFinishHandler.checkFinished(arena))
        {
            return;
        }

        arena.setGamePhase(GamePhase.POST_GAME);
    }

    @Override
    public void gameEnd(final LocalArena arena)
    {
        for (final Map.Entry<UUID, Integer> entry : this.points.entrySet())
        {
            arena.getPlayersManager().broadcast(this.messages, "score.finish.leaderboard_line", entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("points", this.points).toString();
    }
}
