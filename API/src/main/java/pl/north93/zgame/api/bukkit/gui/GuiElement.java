package pl.north93.zgame.api.bukkit.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.gui.impl.IClickable;

public abstract class GuiElement implements IClickable
{
    private GuiElement parent;
    private final List<GuiElement> children;
    
    private final List<String> clickHandlers = new ArrayList<>();
    private final Map<String, String> metadata = new HashMap<>();
    
    private int posX;
    private int posY;
    
    private boolean isDirty = true;
    
    protected GuiElement(boolean canHasChildren)
    {
        children = canHasChildren ? new ArrayList<>() : Arrays.asList();
    }
    
    public final void addChild(GuiElement child)
    {
        Preconditions.checkState(child.parent == null);
        
        child.parent = this;
        children.add(child);
        onElementAdd(child);
        markDirty();
    }
    
    public final void removeChild(GuiElement child)
    {
        Preconditions.checkState(child.parent == this);
        
        child.parent = null;
        children.remove(child);
        onElementRemove(child);
        markDirty();
    }
    
    public int getPosX()
    {
        return posX;
    }
    
    public int getPosY()
    {
        return posY;
    }
    
    public void setPosition(int x, int y)
    {
        this.posX = x;
        this.posY = y;
        markDirty();
    }
    
    public final GuiElement getParent()
    {
        return parent;
    }
    
    public Map<String, String> getMetadata()
    {
        return metadata;
    }
    
    public final List<String> getClickHandlers()
    {
        return clickHandlers;
    }
    
    public final List<GuiElement> getChildren()
    {
        return new ArrayList<>(children);
    }
    
    @SuppressWarnings("unchecked")
    public final <T extends GuiElement> List<T> getChildrenByClass(Class<T> clazz)
    {
        return (List<T>) children.stream().filter(child -> clazz.isInstance(child))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public boolean isDirty()
    {
        return isDirty;
    }
    
    public void markDirty()
    {
        GuiElement parent = this.parent;
        while ( parent != null )
        {
            parent.markDirty();
            parent = parent.parent;
        }
        
        isDirty = true;
    }
    
    public void resetDirty()
    {
        isDirty = false;
    }
    
    protected void onElementAdd(GuiElement element)
    {
    }
    
    protected void onElementRemove(GuiElement element)
    {
    }
    
    public abstract void render(GuiCanvas canvas);
    
    @Override
    public final boolean equals(Object obj)
    {
        return super.equals(obj);
    }
    
    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }
}
