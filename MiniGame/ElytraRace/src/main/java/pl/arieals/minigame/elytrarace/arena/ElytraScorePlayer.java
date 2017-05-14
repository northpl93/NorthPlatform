package pl.arieals.minigame.elytrarace.arena;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.cfg.Score;
import pl.arieals.minigame.elytrarace.cfg.ScoreGroup;

public class ElytraScorePlayer // uzywane w SCORE_MODE
{
    private int          points;
    private ScoreGroup   latestScoreGroup;
    private int          combo;
    private List<Score>  reachedScores        = new ArrayList<>();
    private List<String> reachedAchieveGroups = new ArrayList<>();

    public int getPoints()
    {
        return this.points;
    }

    public void setPoints(final int points)
    {
        this.points = points;
    }

    public void incrementPoints(final int points)
    {
        this.points += points;
    }

    public List<Score> getReachedScores()
    {
        return this.reachedScores;
    }

    public List<String> getReachedAchieveGroups()
    {
        return this.reachedAchieveGroups;
    }

    public Integer getCombo()
    {
        return this.combo;
    }

    public void setCombo(final Integer combo)
    {
        this.combo = combo;
    }

    public int checkCombo(final ScoreGroup scoreGroup)
    {
        if (this.latestScoreGroup == scoreGroup)
        {
            return ++this.combo;
        }
        this.latestScoreGroup = scoreGroup;
        return this.combo = 0;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("points", this.points).append("reachedScores", this.reachedScores).toString();
    }
}
