package pl.arieals.minigame.bedwars.scoreboard;

import java.util.List;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

public class GameScoreboard implements IScoreboardLayout
{
    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return null;
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        return null;
    }

    @Override
    public int updateEvery()
    {
        return 0;
    }
}
