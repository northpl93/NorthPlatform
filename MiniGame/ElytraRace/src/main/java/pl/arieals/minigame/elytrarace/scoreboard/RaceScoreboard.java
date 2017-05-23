package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.cfg.Checkpoint;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

public class RaceScoreboard implements IScoreboardLayout, Listener
{
    public static final RaceScoreboard INSTANCE = new RaceScoreboard();

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
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        final LocalArena arena = getArena(player);

        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.add("&cTime Attack", "&7Czas " + arena.getTimer().humanReadableTimeAfterStart(), "");

        builder.add("Checkpoint " + this.getPlayerCheckpoint(playerData) + "/" + this.getMaxCheckpoints(arena));

        builder.add("", "mc.piraci.pl");

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
}
