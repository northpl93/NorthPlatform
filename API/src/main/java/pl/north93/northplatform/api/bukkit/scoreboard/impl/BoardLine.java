package pl.north93.northplatform.api.bukkit.scoreboard.impl;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class BoardLine
{
    private final Scoreboard scoreboard;
    private final Score      score;
    private final Team       team;
    private       String     text;

    private BoardLine(final Scoreboard scoreboard, final Score score, final Team team)
    {
        this.scoreboard = scoreboard;
        this.score = score;
        this.team = team;
        this.text = "";
    }

    void updateText(final String newText)
    {
        if (this.text.equals(newText))
        {
            return; // nie potrzebujemy aktualizacji
        }
        this.text = newText;

        if (newText.length() <= 16)
        {
            this.team.setPrefix(newText);
            if (! StringUtils.isEmpty(this.team.getSuffix())) // resetujemy suffix tylko gdy jest taka potrzeba
            {
                this.team.setSuffix("");
            }
            return;
        }

        String prefix;
        String suffix;

        String first16 = newText.substring(0, 16);
        if (first16.charAt(15) == '§')
        {
            prefix = newText.substring(0, 15);
            suffix = ChatColor.getLastColors(newText.substring(0, 17));
            suffix += newText.substring(17, newText.length());
        }
        else
        {
            prefix = first16;
            suffix = ChatColor.getLastColors(first16);
            suffix += newText.substring(16, newText.length());
        }

        if (suffix.length() > 16)
        {
            suffix = suffix.substring(0, 16);
        }

        this.team.setPrefix(prefix);
        this.team.setSuffix(suffix);
    }

    void cleanup()
    {
        this.team.unregister();
        this.scoreboard.resetScores(this.score.getEntry());
    }

    /*default*/ static BoardLine newBoardLine(final Objective objective, final String boardId, final int scoreNumber)
    {
        final Scoreboard scoreboard = objective.getScoreboard();

        final Score score = objective.getScore(genScoreName(scoreNumber));
        score.setScore(scoreNumber);

        final Team team = scoreboard.registerNewTeam(boardId + "_" + RandomStringUtils.random(4) + "_" + scoreNumber);
        team.addEntry(score.getEntry());

        return new BoardLine(scoreboard, score, team);
    }

    private static String genScoreName(final int line)
    {
        final char P = ChatColor.COLOR_CHAR;
        String str = line + "";
        return str.length() > 1 ? "" + P + str.charAt(0) + P + str.charAt(1) : "" + P + "0" + P + str.charAt(0);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("scoreboard", this.scoreboard).append("score", this.score).append("team", this.team).toString();
    }
}
