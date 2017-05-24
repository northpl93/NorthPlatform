package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

public class ScoreScoreboard implements IScoreboardLayout
{
    public static final ScoreScoreboard INSTANCE = new ScoreScoreboard();

    private ScoreScoreboard()
    {
    }

    // Score Attack
    //
    // Punkty 68
    //
    // Top3
    // 68 NorthPL93
    // 67 Wodzio
    // -1 lucek_flower
    //
    // mc.piraci.pl
    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&6Elytra Race";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final Player player = context.getPlayer();
        final LocalArena arena = getArena(player);
        final ElytraScorePlayer playerData = getPlayerData(player, ElytraScorePlayer.class);

        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.add("Score Attack", "");

        builder.add("Punkty " + playerData.getPoints());

        for (final Map.Entry<Player, Integer> entry : this.getRanking(arena, 3).entrySet())
        {
            builder.add(entry.getValue() + " " + entry.getKey().getDisplayName());
        }

        builder.add("", "mc.piraci.pl");

        return builder.getContent();
    }

    private Map<Player, Integer> getRanking(final LocalArena arena, int limit)
    {
        final Map<Player, Integer> ranking = new HashMap<>();
        for (final Player player : arena.getPlayersManager().getPlayers())
        {
            final ElytraScorePlayer playerData = getPlayerData(player, ElytraScorePlayer.class);
            ranking.put(player, playerData.getPoints());
        }

        return ranking.entrySet()
                      .stream()
                      .sorted(this::rankingComparator)
                      .limit(limit)
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private int rankingComparator(final Map.Entry<Player, Integer> p1, final Map.Entry<Player, Integer> p2)
    {
        return p1.getValue() - p2.getValue();
    }
}
