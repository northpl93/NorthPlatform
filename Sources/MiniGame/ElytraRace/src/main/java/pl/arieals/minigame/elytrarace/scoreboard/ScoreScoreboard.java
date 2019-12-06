package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class ScoreScoreboard implements IScoreboardLayout
{
    @Inject @Messages("ElytraRace")
    private MessagesBox msg;

    // Score Attack
    //
    // Punkty 68
    //
    // Top3
    // 68 NorthPL93
    // 67 _Wodzio_
    // -1 _Pitbull_
    //
    // mcpiraci.pl
    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&e&lScore Attack";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final Player player = context.getPlayer();
        final LocalArena arena = getArena(player);

        final ElytraRacePlayer racePlayer = getPlayerData(player, ElytraRacePlayer.class);
        if (arena == null || racePlayer == null)
        {
            return Collections.emptyList();
        }

        final ElytraScorePlayer scorePlayer = racePlayer.asScorePlayer();

        final ContentBuilder builder = IScoreboardLayout.builder();
        builder.box(this.msg).locale(player.getLocale());

        builder.add("");
        builder.translated("scoreboard.score.points", scorePlayer.getPoints());
        builder.add("");
        builder.translated("scoreboard.score.top");

        final Map<ElytraScorePlayer, Integer> ranking = this.getRanking(arena, 5);

        // maksymalna ilosc punktów posiadanych przez gracza w tym rankingu
        final int max = ranking.values().iterator().next();

        for (final Map.Entry<ElytraScorePlayer, Integer> entry : ranking.entrySet())
        {
            final String displayName = entry.getKey().getPlayer().getDisplayName();
            builder.translated("scoreboard.score.top_line", this.align(max, entry.getValue()), displayName);
        }

        builder.add("");
        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    private String align(final int max, final int points)
    {
        final int maxLength = Math.max(2, String.valueOf(max).length()); // 2
        final String pointsString = String.valueOf(points); // 3
        return StringUtils.repeat('0', Math.max(0, maxLength - pointsString.length())) + pointsString;
    }

    private Map<ElytraScorePlayer, Integer> getRanking(final LocalArena arena, final int limit)
    {
        final Map<ElytraScorePlayer, Integer> ranking = new LinkedHashMap<>();

        final ElytraRaceArena arenaData = arena.getArenaData();
        for (final ElytraRacePlayer racePlayer : arenaData.getPlayers())
        {
            final ElytraScorePlayer playerData = racePlayer.asScorePlayer();
            ranking.put(playerData, playerData.getPoints());
        }

        return ranking.entrySet()
                      .stream()
                      .sorted(this::rankingComparator)
                      .limit(limit)
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private int rankingComparator(final Map.Entry<ElytraScorePlayer, Integer> p1, final Map.Entry<ElytraScorePlayer, Integer> p2)
    {
        return p2.getValue() - p1.getValue();
    }

    @Override
    public int updateEvery()
    {
        return 10;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
