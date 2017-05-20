package pl.north93.zgame.api.bukkit.scoreboard;

import java.util.List;

public interface IScoreboardLayout
{
    String getTitle(IScoreboardContext context);

    List<String> getContent(IScoreboardContext context);

    default int updateEvery()
    {
        return 0;
    }

    static ContentBuilder builder()
    {
        return new ContentBuilder();
    }
}
