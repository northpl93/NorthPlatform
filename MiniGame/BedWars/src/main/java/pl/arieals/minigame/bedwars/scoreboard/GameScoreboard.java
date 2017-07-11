package pl.arieals.minigame.bedwars.scoreboard;

import static java.util.Comparator.comparing;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.zgame.api.bukkit.utils.ChatUtils.stripColor;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.arieals.api.minigame.server.gamehost.arena.DeathMatch;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BedWarsConfig;
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
    @Inject
    private BedWarsConfig config;
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
            this.buildDeathMatchStatus(builder, arena);
        }
        else
        {
            this.buildGeneratorUpgrade(builder, nextUpgrade, arena, context.getLocale());
        }

        this.buildTeamList(builder, arenaData, context.getLocale());

        builder.add("");
        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    private void buildDeathMatchStatus(final ContentBuilder builder, final LocalArena arena)
    {
        final DeathMatch deathMatch = arena.getDeathMatch();
        if (deathMatch.getState() == DeathMatchState.NOT_STARTED)
        {
            final long timeTo = arena.getTimer().calcTimeTo(this.config.getStartDeathMatchAt() * 50, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
            final String humanTime = FORMAT.format(LocalDateTime.ofEpochSecond(timeTo, 0, ZoneOffset.UTC));

            builder.translated("scoreboard.deathmatch.countdown", humanTime);
        }
        else if (deathMatch.getState().isActive())
        {
            builder.translated("scoreboard.deathmatch.title");
            if (deathMatch.isFightActive())
            {
                builder.translated("scoreboard.deathmatch.fight");
            }
            else
            {
                final String timeToStart = deathMatch.getFightManager() == null ? "" : String.valueOf(deathMatch.getFightManager().getTimeToStart());
                builder.translated("scoreboard.deathmatch.prepare", timeToStart);
            }
        }
    }

    private void buildGeneratorUpgrade(final ContentBuilder builder, final Pair<BedWarsGeneratorType, BedWarsGeneratorItemConfig> nextUpgrade, final LocalArena arena, final String locale)
    {
        final String generatorName = this.messages.getMessage(locale, "generator.type.nominative." + nextUpgrade.getKey().getName());
        final long timeTo = (nextUpgrade.getValue().getStartAt() / 20) - arena.getTimer().getCurrentTime(TimeUnit.SECONDS);
        final String humanTime = FORMAT.format(LocalDateTime.ofEpochSecond(timeTo, 0, ZoneOffset.UTC));

        builder.translated("scoreboard.upgrade",
                stripColor(generatorName),
                nextUpgrade.getValue().getName(),
                humanTime);
    }

    private void buildTeamList(final ContentBuilder builder, final BedWarsArena arenaData, final String locale)
    {
        builder.add("");
        arenaData.getTeams().stream().sorted(comparing(Team::getScoreboardOrder)).forEach(team ->
        {
            final String teamName = this.messages.getMessage(locale, "team.scoreboard." + team.getName());
            final String status;

            if (team.isBedAlive())
            {
                status = this.messages.getMessage(locale, "scoreboard.tick");
            }
            else
            {
                final int players = team.getAlivePlayers().size();
                if (players == 0)
                {
                    status = this.messages.getMessage(locale, "scoreboard.cross");
                }
                else
                {
                    status = "&a" + players;
                }
            }

            builder.translated("scoreboard.team_line", team.getColor().getChar(), teamName, status);
        });
    }

    @Override
    public int updateEvery()
    {
        return 20;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
