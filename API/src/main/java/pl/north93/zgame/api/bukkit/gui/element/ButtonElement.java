package pl.north93.zgame.api.bukkit.gui.element;

import pl.north93.zgame.api.bukkit.gui.GuiCanvas;
import pl.north93.zgame.api.bukkit.gui.IGuiIcon;

public class ButtonElement extends GuiElement
{
    private IGuiIcon icon;
    
    public ButtonElement(IGuiIcon icon)
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
    protected void render0(GuiCanvas content)
    {
        content.setEntry(getPosX(), getPosY(), icon, this);
    }
}
