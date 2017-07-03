package pl.arieals.minigame.bedwars.scoreboard;

import static java.util.Comparator.comparing;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.zgame.api.bukkit.utils.ChatUtils.stripColor;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorItemConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorType;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class GameScoreboard implements IScoreboardLayout
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("[mm:]ss");
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&e&lBEDWARS";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final ContentBuilder builder = IScoreboardLayout.builder();
        builder.box(this.messages).locale(context.getLocale());
        builder.add("");

        final LocalArena arena = getArena(context.getPlayer());
        final BedWarsArena arenaData = arena.getArenaData();

        final Pair<BedWarsGeneratorType, BedWarsGeneratorItemConfig> nextUpgrade = arenaData.nextUpgrade();
        if (nextUpgrade == null)
        {
            // todo deadmatch
        }
        else
        {
            final String generatorName = this.messages.getMessage(context.getLocale(), "generator.type." + nextUpgrade.getKey().getName());
            final long timeTo = (nextUpgrade.getValue().getStartAt() / 20) - arena.getTimer().getCurrentTime(TimeUnit.SECONDS);
            final String humanTime = FORMAT.format(LocalDateTime.ofEpochSecond(timeTo, 0, ZoneOffset.UTC));

            builder.translated("scoreboard.upgrade",
                    stripColor(generatorName),
                    nextUpgrade.getValue().getName(),
                    humanTime);
        }

        builder.add("");
        arenaData.getTeams().stream().sorted(comparing(Team::getColor)).forEach(team ->
        {
            final String teamName = this.messages.getMessage(context.getLocale(), "team." + team.getName());
            final String status;

            if (team.isBedAlive())
            {
                status = this.messages.getMessage(context.getLocale(), "scoreboard.tick");
            }
            else
            {
                final int players = team.getAlivePlayers().size();
                if (players == 0)
                {
                    status = this.messages.getMessage(context.getLocale(), "scoreboard.cross");
                }
                else
                {
                    status = "&a" + players;
                }
            }

            builder.translated("scoreboard.team_line", team.getColor().getChar(), teamName, status);
        });

        builder.add("");
        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    @Override
    public int updateEvery()
    {
        return 20;
    }
}
