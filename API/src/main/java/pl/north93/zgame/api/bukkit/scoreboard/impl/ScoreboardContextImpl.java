package pl.north93.zgame.api.bukkit.scoreboard.impl;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

import static pl.north93.zgame.api.bukkit.scoreboard.impl.BoardLine.newBoardLine;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

class ScoreboardContextImpl implements IScoreboardContext
{
    private final Player              player;
    private final IScoreboardLayout   layout;
    private final Map<String, Object> data;
    // scoreboard logic
    private final String              boardId;
    private final List<BoardLine>     boardLines;

    public ScoreboardContextImpl(final Player player, final IScoreboardLayout layout)
    {
        this.player = player;
        this.layout = layout;
        this.data = new HashMap<>();
        this.boardId = RandomStringUtils.random(4);
        this.boardLines = new LinkedList<>();
    }

    @Override
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public IScoreboardLayout getLayout()
    {
        return this.layout;
    }

    @Override
    public void set(final String key, final Object value)
    {
        this.data.put(key, value);
        this.update();
    }

    @Override
    public void set(final Map<String, Object> data)
    {
        this.data.putAll(data);
        this.update();
    }

    @Override
    public <T> T get(final String key)
    {
        //noinspection unchecked
        return (T) this.data.get(key);
    }

    @Override
    public void update()
    {
        final Scoreboard board = this.player.getScoreboard();

        final String title = translateAlternateColorCodes('&', this.layout.getTitle(this));
        final List<String> content = this.layout.getContent(this);

        final Objective objective = this.getObjective(board);
        objective.setDisplayName(title);

        this.checkLines(objective, content.size());

        final Iterator<BoardLine> linesIterator = this.boardLines.iterator();
        for (final String aContent : content)
        {
            // rozmiar this.boardLines i content jest taki sam; dba o to checkLines
            final BoardLine boardLine = linesIterator.next();

            boardLine.updateText(ChatColor.translateAlternateColorCodes('&', aContent));
        }
    }

    private Objective getObjective(final Scoreboard scoreboard)
    {
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null)
        {
            objective = scoreboard.registerNewObjective(this.boardId, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        return objective;
    }

    private void checkLines(final Objective objective, final int required)
    {
        final int actual = this.boardLines.size();

        if (actual > required)
        {
            final int diff = actual - required;

            int removed = 0;
            final ListIterator<BoardLine> iter = this.boardLines.listIterator(actual);
            while (removed != diff && iter.hasPrevious())
            {
                final BoardLine previous = iter.previous();
                previous.cleanup();
                iter.remove();
            }
        }
        else if (actual < required)
        {
            final int diff = required - actual;

            for (int i = diff; i != 0; i--)
            {
                this.boardLines.add(newBoardLine(objective, this.boardId, actual + i));
            }
        }
    }

    /*default*/ void cleanup()
    {
        this.boardLines.forEach(BoardLine::cleanup);
        this.boardLines.clear();
        this.getObjective(this.player.getScoreboard()).unregister();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("layout", this.layout).append("data", this.data).toString();
    }
}
