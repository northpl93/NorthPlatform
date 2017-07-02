package pl.arieals.minigame.bedwars.scoreboard;

import java.util.Arrays;
import java.util.List;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

public class LobbyScoreboard implements IScoreboardLayout
{
    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&eBEDWARS";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        return Arrays.asList();
    }

    @Override
    public int updateEvery()
    {
        return 20;
    }
}
