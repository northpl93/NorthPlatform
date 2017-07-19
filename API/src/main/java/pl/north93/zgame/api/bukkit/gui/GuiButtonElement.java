package pl.north93.zgame.api.bukkit.gui;

public class GuiButtonElement extends GuiElement
{
    private GuiIcon icon;
    
    public GuiButtonElement(GuiIcon icon)
    {
        super(false);
        this.icon = icon;
    }
    
    public GuiIcon getIcon()
    {
        return icon;
    }
    
    public void setIcon(GuiIcon icon)
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
