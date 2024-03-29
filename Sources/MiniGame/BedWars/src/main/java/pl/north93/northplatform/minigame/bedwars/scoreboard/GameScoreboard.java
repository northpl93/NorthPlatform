package pl.north93.northplatform.minigame.bedwars.scoreboard;

import static java.util.Comparator.comparing;

import static pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils.stripColor;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.north93.northplatform.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.world.DeathMatch;
import pl.north93.northplatform.api.minigame.shared.api.arena.DeathMatchState;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;
import pl.north93.northplatform.minigame.bedwars.arena.Team;
import pl.north93.northplatform.minigame.bedwars.cfg.BwConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGeneratorItemConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGeneratorType;

public class GameScoreboard implements IScoreboardLayout
{
    private static final DateTimeFormatter FORMAT = new DateTimeFormatterBuilder().appendValue(ChronoField.MINUTE_OF_HOUR, 1, 2, SignStyle.NORMAL).appendPattern("':'ss").toFormatter();
    @Inject
    private BwConfig    config;
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

        final Pair<BwGeneratorType, BwGeneratorItemConfig> nextUpgrade = arenaData.nextUpgrade();
        if (nextUpgrade == null)
        {
            this.buildDeathMatchStatus(builder, arena);
        }
        else
        {
            this.buildGeneratorUpgrade(builder, nextUpgrade, arena, context.getLocale());
        }

        this.buildTeamList(builder, arenaData, context.getPlayer());

        builder.add("");
        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    private void buildDeathMatchStatus(final ContentBuilder builder, final LocalArena arena)
    {
        final DeathMatch deathMatch = arena.getDeathMatch();
        if (deathMatch.getState() == DeathMatchState.NOT_STARTED)
        {
            // przed deathmatchem jest jeszcze niszczenie lozek
            final long timeToDestroyBeds = arena.getTimer().calcTimeTo(this.config.getDestroyBedsAt()* 50L, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
            if (timeToDestroyBeds >= 0)
            {
                final String humanTime = FORMAT.format(LocalDateTime.ofEpochSecond(timeToDestroyBeds, 0, ZoneOffset.UTC));
                builder.translated("scoreboard.beds_destroy", humanTime);
            }
            else
            {
                final long timeTo = arena.getTimer().calcTimeTo(this.config.getStartDeathMatchAt() * 50L, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
                final String humanTime = FORMAT.format(LocalDateTime.ofEpochSecond(timeTo, 0, ZoneOffset.UTC));

                builder.translated("scoreboard.deathmatch.countdown", humanTime);
            }
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

    private void buildGeneratorUpgrade(final ContentBuilder builder, final Pair<BwGeneratorType, BwGeneratorItemConfig> nextUpgrade, final LocalArena arena, final String locale)
    {
        final String generatorName = this.messages.getString(locale, "generator.type.nominative." + nextUpgrade.getKey().getName());
        final long timeTo = (nextUpgrade.getValue().getStartAt() / 20) - arena.getTimer().getCurrentTime(TimeUnit.SECONDS);
        final String humanTime = FORMAT.format(LocalDateTime.ofEpochSecond(timeTo, 0, ZoneOffset.UTC));

        builder.translated("scoreboard.upgrade",
                stripColor(generatorName),
                nextUpgrade.getValue().getName(),
                humanTime);
    }

    private void buildTeamList(final ContentBuilder builder, final BedWarsArena arenaData, final Player renderingPlayer)
    {
        final String locale = renderingPlayer.getLocale();

        builder.add("");
        arenaData.getTeams().stream().sorted(comparing(Team::getScoreboardOrder)).forEach(team ->
        {
            final String teamName = this.messages.getString(locale, "team.scoreboard." + team.getName());

            final String status;
            if (team.isBedAlive())
            {
                status = this.messages.getString(locale, "scoreboard.tick");
            }
            else if (team.isEliminated())
            {
                status = this.messages.getString(locale, "scoreboard.cross");
            }
            else
            {
                status = "&a" + team.getNotEliminatedPlayers().size();
            }

            final boolean renderFlag = !team.isEliminated() && (!team.isBedAlive() || team.getBukkitPlayers().contains(renderingPlayer));
            final int lives = team.countAdditionalLives();
            if (renderFlag && lives > 0)
            {
                builder.translated("scoreboard.team_line_lives", team.getColor(), teamName, status, lives);
            }
            else
            {
                builder.translated("scoreboard.team_line", team.getColor(), teamName, status);
            }
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
