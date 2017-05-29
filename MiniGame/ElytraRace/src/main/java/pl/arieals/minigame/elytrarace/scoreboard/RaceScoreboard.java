package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.List;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class RaceScoreboard implements IScoreboardLayout
{
    @Inject
    @Messages("ElytraRace")
    private MessagesBox msg;

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&e&lTime Attack";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final Player player = context.getPlayer();
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        final LocalArena arena = getArena(player);
        final ElytraRaceArena arenaData = arena.getArenaData();

        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.box(this.msg).locale(player.spigot().getLocale());

        builder.add("");
        builder.translated("scoreboard.race.time", arena.getTimer().humanReadableTimeAfterStart());
        builder.translated("scoreboard.race.avg_time", arena.getTimer().humanReadableTimeAfterStart());
        builder.add("");
        builder.translated("scoreboard.race.checkpoint", playerData.getCheckpointNumber(), arenaData.getMaxCheckpoints());
        builder.add("");
        builder.translated("scoreboard.ip");

        return builder.getContent();
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
