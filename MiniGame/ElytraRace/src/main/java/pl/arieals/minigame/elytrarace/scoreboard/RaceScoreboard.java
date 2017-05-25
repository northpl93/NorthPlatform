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
import pl.arieals.minigame.elytrarace.cfg.Checkpoint;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class RaceScoreboard implements IScoreboardLayout
{
    public static final RaceScoreboard INSTANCE = new RaceScoreboard();
    @InjectMessages("ElytraRace")
    private MessagesBox msg;

    private RaceScoreboard()
    {
    }

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&6Elytra Race";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final Player player = context.getPlayer();
        final String locale = player.spigot().getLocale();
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        final LocalArena arena = getArena(player);

        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.add(
                "&cTime Attack",
                this.msg.getMessage(locale, "scoreboard.race.time", arena.getTimer().humanReadableTimeAfterStart()),
                "",
                this.msg.getMessage(locale, "scoreboard.race.checkpoint", this.getPlayerCheckpoint(playerData), this.getMaxCheckpoints(arena)),
                "",
                this.msg.getMessage(locale, "scoreboard.ip"));

        return builder.getContent();
    }

    private int getPlayerCheckpoint(final ElytraRacePlayer playerData)
    {
        final int checkpointNumber;
        final Checkpoint checkpoint = playerData.getCheckpoint();
        if (checkpoint == null)
        {
            checkpointNumber = 0;
        }
        else
        {
            checkpointNumber = checkpoint.getNumber();
        }
        return checkpointNumber;
    }

    private int getMaxCheckpoints(final LocalArena arena)
    {
        final ElytraRaceArena arenaData = arena.getArenaData();
        return arenaData.getArenaConfig().getCheckpoints().stream().mapToInt(Checkpoint::getNumber).max().orElse(0);
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
