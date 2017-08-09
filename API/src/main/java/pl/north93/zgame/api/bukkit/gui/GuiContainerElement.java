package pl.north93.zgame.api.bukkit.gui;

public class GuiContainerElement extends GuiElement
{
    private IGuiIcon background;
    private IGuiIcon border;
    
    private final GuiCanvas canvas;
    
    public GuiContainerElement(int width, int height)
    {
        super(true);
        this.canvas = new GuiCanvas(width, height);
    }
    
    public int getWidht()
    {
        return canvas.getWidth();
    }
    
    public int getHeight()
    {
        return canvas.getHeight();
    }
    
    public void setSize(int width, int height)
    {
        canvas.resize(width, height);
        markDirty();
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
    protected void render0(GuiCanvas canvas)
    {
        this.canvas.clear();
        getChildren().forEach(child -> child.render(this.canvas));
        
        if ( border != null )
        {
            renderBorder(canvas);
        }
        if ( background != null )
        {
            renderBackground(canvas);
        }
        
        // TODO: paginate
        canvas.paste(getPosX(), getPosY(), this.canvas, true);
    }
    
    private void renderBorder(GuiCanvas canvas)
    {
        for ( int i = 0; i < this.canvas.getHeight() + 2; i++ )
        {
            canvas.setEntry(getPosX() - 1, getPosY() - 1 + i, border, this);
            canvas.setEntry(getPosX() + getWidht(), getPosY() - 1 + i, border, this);
        }
        for ( int i = 0; i < this.canvas.getWidth() + 2; i++ )
        {
            canvas.setEntry(getPosX() - 1 + i, getPosY() - 1, border, this);
            canvas.setEntry(getPosX() - 1 + i, getPosY() + getHeight(), border, this);
        }
    }
    
    private void renderBackground(GuiCanvas canvas)
    {
        for ( int i = 0; i < this.canvas.getWidth(); i++ )
        {
            for ( int j = 0; j < this.canvas.getHeight(); j++ )
            {
                canvas.setEntry(getPosX() + i, getPosY() + j, background, this);
            }
        }
    }
}
