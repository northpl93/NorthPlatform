package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ScoreScoreboard implements IScoreboardLayout
{
    @Inject
    @Messages("ElytraRace")
    private MessagesBox msg;

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
        builder.box(this.msg).locale(player.spigot().getLocale());

        builder.add("Score Attack", "");
        builder.translated("scoreboard.score.points", playerData.getPoints());
        builder.add("");
        builder.translated("scoreboard.score.top3");

        for (final Map.Entry<Player, Integer> entry : this.getRanking(arena, 3).entrySet())
        {
            builder.translated("scoreboard.score.top3_line", entry.getValue(), entry.getKey().getDisplayName());
        }

        builder.add("");
        builder.translated("scoreboard.ip");

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
