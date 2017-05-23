package pl.arieals.minigame.elytrarace.scoreboard;

import java.util.List;

import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

public class ScoreScoreboard implements IScoreboardLayout
{
    public static final ScoreScoreboard INSTANCE = new ScoreScoreboard();

    private ScoreScoreboard()
    {
    }

    // Score Attack
    //
    // Punkty 68
    //
    // Top3
    // 68 NorthPL93
    // 67 Wodzio
    // -1 lucek_flower
    //
    // mc.piraci.pl
    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&6Elytra Race";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.add("Score Attack");

        return builder.getContent();
    }
}
