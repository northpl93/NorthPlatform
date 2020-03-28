package pl.north93.northplatform.minigame.elytrarace.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.elytrarace.cfg.Score;
import pl.north93.northplatform.minigame.elytrarace.cfg.ScoreGroup;
import pl.north93.northplatform.minigame.elytrarace.shop.effects.IElytraEffect;

public class ElytraScorePlayer extends ElytraRacePlayer // uzywane w SCORE_MODE
{
    private int          points;
    private ScoreGroup latestScoreGroup;
    private int          combo;
    private List<Score>  reachedScores        = new ArrayList<>();
    private List<String> reachedAchieveGroups = new ArrayList<>();

    public ElytraScorePlayer(final Player player, final IElytraEffect effect, final Location startLocation)
    {
        super(player, effect, startLocation);
    }

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
