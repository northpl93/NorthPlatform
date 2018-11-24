package pl.north93.northplatform.api.bukkit.map.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.bukkit.map.IMapRenderer;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@Slf4j
class RendererScheduler
{
    @Inject
    private       IBukkitExecutor          executor;
    private final MapController            mapController;
    private final List<InProgressRenderer> renderers = new ArrayList<>();

    public RendererScheduler(final MapController mapController)
    {
        this.mapController = mapController;
    }

    public synchronized void scheduleRenderer(final INorthPlayer player, final BoardImpl board)
    {
        Preconditions.checkNotNull(player, "Player can't be null");
        Preconditions.checkNotNull(board.getRenderer(), "Tried to schedule rendering when renderer is null");

        final InProgressRenderer progress = new InProgressRenderer(player, board, board.getRenderer());
        this.renderers.add(progress);

        this.executor.async(() -> this.doRendering(progress));
    }

    private void doRendering(final InProgressRenderer inProgressRenderer)
    {
        final INorthPlayer player = inProgressRenderer.getPlayer();
        final BoardImpl board = inProgressRenderer.getBoard();

        final MapCanvasImpl canvas = MapCanvasImpl.createFromMaps(board.getWidth(), board.getHeight());
        try
        {
            inProgressRenderer.getRenderer().render(canvas, player);
        }
        catch (final Exception e)
        {
            log.error("An exception has been throw in map renderer", e);
        }

        synchronized (this)
        {
            this.renderers.remove(inProgressRenderer);
            if (inProgressRenderer.isAborted())
            {
                return;
            }

            this.mapController.pushNewCanvasToBoardForPlayer(player, board, canvas);
        }
    }

    public synchronized boolean abortIfRenderingInProgress(final BoardImpl board, final Player player)
    {
        final InProgressRenderer progress = this.getProgress(board, player);
        if (progress == null)
        {
            return false;
        }
        progress.setAborted(true);
        this.renderers.remove(progress);
        return true;
    }

    public synchronized boolean isRendererScheduled(final BoardImpl board, final Player player)
    {
        return this.getProgress(board, player) != null;
    }

    private synchronized InProgressRenderer getProgress(final BoardImpl board, final Player player)
    {
        for (final InProgressRenderer renderer : this.renderers)
        {
            if (renderer.getBoard() == board && player.equals(renderer.getPlayer()))
            {
                return renderer;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("renderers", this.renderers).toString();
    }
}

class InProgressRenderer
{
    private final INorthPlayer player;
    private final BoardImpl    board;
    private final IMapRenderer renderer;
    private       boolean      aborted;

    public InProgressRenderer(final INorthPlayer player, final BoardImpl board, final IMapRenderer renderer)
    {
        this.player = player;
        this.board = board;
        this.renderer = renderer;
    }

    public INorthPlayer getPlayer()
    {
        return this.player;
    }

    public BoardImpl getBoard()
    {
        return this.board;
    }

    public IMapRenderer getRenderer()
    {
        return this.renderer;
    }

    public boolean isAborted()
    {
        return this.aborted;
    }

    public void setAborted(final boolean aborted)
    {
        this.aborted = aborted;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("board", this.board).append("renderer", this.renderer).append("aborted", this.aborted).toString();
    }
}