package pl.north93.zgame.api.bukkit.map.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import pl.north93.zgame.api.bukkit.map.IMapManager;
import pl.north93.zgame.api.global.component.Component;

public class MapManagerImpl extends Component implements IMapManager
{
    private final List<BoardImpl> boards = new ArrayList<>();

    @Override
    public BoardImpl createBoard(final Location leftCorner, final Location rightCorner)
    {
        final BoardImpl board = BoardFactory.createBoard(leftCorner, rightCorner);
        this.boards.add(board);
        return board;
    }

    @Override
    protected void enableComponent()
    {

    }

    @Override
    protected void disableComponent()
    {

    }
}
