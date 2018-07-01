package pl.north93.zgame.api.bukkit.map.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.map.IBoard;
import pl.north93.zgame.api.bukkit.map.IMapManager;
import pl.north93.zgame.api.global.component.Component;

public class MapManagerImpl extends Component implements IMapManager
{
    private final List<BoardImpl> boards = new ArrayList<>();

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public BoardImpl createBoard(final Location leftCorner, final Location rightCorner)
    {
        final BoardImpl board = BoardFactory.createBoard(leftCorner, rightCorner);
        this.boards.add(board);
        return board;
    }

    @Override
    public Collection<BoardImpl> getBoards()
    {
        return new ArrayList<>(this.boards);
    }

    @Override
    public void removeBoard(final IBoard board)
    {
        final BoardImpl boardImpl = (BoardImpl) board;
        if (this.boards.remove(boardImpl))
        {
            boardImpl.cleanup();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
