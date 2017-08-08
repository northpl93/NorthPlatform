package pl.north93.zgame.api.bukkit.gui;

public class GuiButtonElement extends GuiElement
{
    private IGuiIcon icon;
    
    public GuiButtonElement(IGuiIcon icon)
    {
        super(false);
        this.icon = icon;
    }
    
    public IGuiIcon getIcon()
    {
        return icon;
    }
    
    public void setIcon(IGuiIcon icon)
    {
        this.icon = icon;
        markDirty();
    }
    
    @Override
    public void render(GuiCanvas content)
    {
        content.setEntry(getPosX(), getPosY(), icon, this);
    }
}
