package pl.arieals.api.minigame.server.gamehost.lobby;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

public class LobbyScoreboard implements IScoreboardLayout
{
    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&aWitaj " + context.getPlayer().getName();
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final LocalArena arena = getArena(context.getPlayer());
        return Arrays.asList(
                "Start za",
                String.valueOf(arena.getTimer().getCurrentTime(TimeUnit.SECONDS)),
                "",
                "Ilosc graczy",
                String.valueOf(arena.getPlayers().size()));
    }
}
