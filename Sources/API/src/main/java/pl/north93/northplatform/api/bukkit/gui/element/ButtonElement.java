package pl.north93.northplatform.api.bukkit.gui.element;

import pl.north93.northplatform.api.bukkit.gui.GuiCanvas;
import pl.north93.northplatform.api.bukkit.gui.IGuiIcon;

public class ButtonElement extends GuiElement
{
    private IGuiIcon icon;
    
    private String ifVar;
    private boolean negated;
    
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
    
    public void setIfVar(String ifVar)
    {
        this.ifVar = ifVar;
    }
    
    public void setNegated(boolean negated)
    {
        this.negated = negated;
    }
    
    public String getIfVar()
    {
        return ifVar;
    }
    
    public boolean isNegated()
    {
        return negated;
    }
    
    // TODO: move this to GuiElement
    protected final boolean shouldRender()
    {
        if (this.ifVar == null)
        {
            return true;
        }
 
        boolean evaluatedVariable = Boolean.valueOf(getVariables().getValue(this.ifVar).toString());
        if (this.negated)
        {
            evaluatedVariable = !evaluatedVariable;
        }
        return evaluatedVariable;
    }
    
    @Override
    protected void render0(GuiCanvas content)
    {
        if ( shouldRender() )
        {
            content.setEntry(getPosX(), getPosY(), icon, this);
        }
    }
}
