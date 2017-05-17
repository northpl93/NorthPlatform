package pl.arieals.minigame.elytrarace.arena.meta;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;

public class ScoreMetaHandler implements IFinishHandler
{
    private Map<UUID, Integer> points; // uzywane w SCORE_MODE do przyznawania nagrod nawet gdy gracz wyjdzie

    @Override
    public void handle(final LocalArena arena, final Player player)
    {
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        playerData.setFinished(true);

        final ElytraScorePlayer scoreData = getPlayerData(player, ElytraScorePlayer.class);

        player.sendMessage("Masz " + scoreData.getPoints() + " punktów!");
        this.points.put(player.getUniqueId(), scoreData.getPoints());

        if (! IFinishHandler.checkFinished(arena))
        {
            return;
        }

        arena.setGamePhase(GamePhase.POST_GAME);
    }
}
