package pl.north93.northplatform.api.bukkit.gui.element;

import com.google.common.base.Preconditions;

import pl.north93.northplatform.api.bukkit.gui.GuiCanvas;
import pl.north93.northplatform.api.bukkit.gui.IGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.IPageable;

public abstract class ContainerElement extends GuiElement implements IPageable
{
    private int      width, height;
    private IGuiIcon background;
    private IGuiIcon border;
    private int      page;

    public ContainerElement(final boolean canHasChildren, int width, int height)
    {
        super(canHasChildren);
        this.width = width;
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        markDirty();
    }

    @Override
    public int getPage()
    {
        return this.page;
    }

    @Override
    public void setPage(int newPage)
    {
        Preconditions.checkState(newPage >= 0, "Page number must be 0 or greater");
        this.page = newPage;
        markDirty();
    }

    @Override
    public void nextPage()
    {
        this.setPage(this.page + 1);
    }

    @Override
    public void previousPage()
    {
        if (this.page == 0)
        {
            return; // po cichu ignorujemy zamiast rzucać wyjątek z setPage
        }
        this.setPage(this.page - 1);
    }

    public void setBackground(IGuiIcon background)
    {
        this.background = background;
        markDirty();
    }

    public void setBorder(IGuiIcon border)
    {
        this.border = border;
        markDirty();
    }

    @Override
    protected final void render0(GuiCanvas canvas)
    {
        final GuiCanvas tempCanvas = new GuiCanvas(this.getWidth(), this.getHeight(), this.getHeight() * this.page);
        this.renderElements(tempCanvas);

        if ( this.border != null )
        {
            renderBorder(canvas);
        }
        if ( background != null )
        {
            renderBackground(canvas);
        }

        canvas.paste(getPosX(), getPosY(), tempCanvas, true);
    }

    protected abstract void renderElements(GuiCanvas canvas);

    private void renderBorder(GuiCanvas canvas)
    {
        for ( int i = 0; i < this.getHeight() + 2; i++ )
        {
            canvas.setEntry(getPosX() - 1, getPosY() - 1 + i, border, this);
            canvas.setEntry(getPosX() + getWidth(), getPosY() - 1 + i, border, this);
        }
        for ( int i = 0; i < this.getWidth() + 2; i++ )
        {
            canvas.setEntry(getPosX() - 1 + i, getPosY() - 1, border, this);
            canvas.setEntry(getPosX() - 1 + i, getPosY() + getHeight(), border, this);
        }
    }

    private void renderBackground(GuiCanvas canvas)
    {
        for ( int i = 0; i < this.getWidth(); i++ )
        {
            for ( int j = 0; j < this.getHeight(); j++ )
            {
                canvas.setEntry(getPosX() + i, getPosY() + j, background, this);
            }
        }
    }
}
