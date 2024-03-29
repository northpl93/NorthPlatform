package pl.north93.northplatform.api.bukkit.scoreboard.impl;

import static pl.north93.northplatform.api.bukkit.scoreboard.impl.BoardLine.newBoardLine;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;

class ScoreboardContextImpl implements IScoreboardContext
{
    private final IBukkitExecutor       executor;
    private final INorthPlayer          player;
    private final IScoreboardLayout     layout;
    private final Map<String, Object>   data;
    // scoreboard logic
    private final String                boardId;
    private final LinkedList<BoardLine> boardLines;

    public ScoreboardContextImpl(final IBukkitExecutor executor, final INorthPlayer player, final IScoreboardLayout layout)
    {
        this.executor = executor;
        this.player = player;
        this.layout = layout;
        this.data = new HashMap<>();
        this.boardId = RandomStringUtils.random(4);
        this.boardLines = new LinkedList<>();
        layout.initContext(this);
    }

    @Override
    public INorthPlayer getPlayer()
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
    public <T> void setCompletableFuture(final String key, final CompletableFuture<T> future)
    {
        this.data.put(key, future);
        future.thenRun(() -> this.executor.inMainThread(this::update));
    }

    @Override
    public <T> Optional<T> getCompletableFuture(final String key)
    {
        final CompletableFuture<T> future = this.get(key); // ew. jebnie ClassCastException gdy to jednak nie completablefuture.
        return Optional.ofNullable(future.getNow(null));
    }

    @Override
    public void update()
    {
        final Scoreboard board = this.player.getScoreboard();

        final String title = ChatUtils.translateAlternateColorCodes(this.layout.getTitle(this));
        final List<String> content = this.layout.getContent(this);

        final Objective objective = this.getObjective(board);
        objective.setDisplayName(title);

        this.checkLines(objective, content.size());

        final Iterator<BoardLine> linesIterator = this.boardLines.iterator();
        for (final String aContent : content)
        {
            // rozmiar this.boardLines i content jest taki sam; dba o to checkLines
            final BoardLine boardLine = linesIterator.next();

            boardLine.updateText(ChatUtils.translateAlternateColorCodes(aContent));
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
            while (removed++ != diff && iter.hasPrevious())
            {
                final BoardLine previous = iter.previous();
                previous.cleanup();
                iter.remove();
            }
        }
        else if (actual < required)
        {
            final int diff = required - actual;

            for (int i = 0; i < diff; i++)
            {
                this.boardLines.addFirst(newBoardLine(objective, this.boardId, actual + i));
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
