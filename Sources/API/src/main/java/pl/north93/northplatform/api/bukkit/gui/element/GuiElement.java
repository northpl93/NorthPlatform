package pl.north93.northplatform.api.bukkit.gui.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.bukkit.gui.GuiCanvas;
import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickHandler;
import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickable;

public abstract class GuiElement implements IClickable
{
    private GuiElement parent;
    private final List<GuiElement> children;
    
    private final List<IClickHandler> clickHandlers = new ArrayList<>();
    private final Map<String, String> metadata = new HashMap<>();

    // Nie da się jeszcze ich ustawiać w XMLu, są dostępne tylko leniwe <variable>
    private Vars<Object> variables = Vars.empty();

    private int posX;
    private int posY;
    
    private boolean isDirty = true;
    
    private boolean visible = true;
    
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
    
    public final Vars<Object> getVariables()
    {
        if (this.parent != null)
        {
            // pobieramy rekurencyjnie wszystkie zmienne od naszych rodziców, one też nas interesują
            return this.variables.and(this.parent.getVariables());
        }
        return this.variables;
    }
    
    public void setVariables(Vars<Object> variables)
    {
        this.variables = variables;
    }
    
    public void addVariables(Vars<Object> localVariables)
    {
        this.variables = this.variables.and(localVariables);
    }
    
    public Map<String, String> getMetadata()
    {
        return metadata;
    }
    
    public final List<IClickHandler> getClickHandlers()
    {
        return clickHandlers;
    }
    
    public final List<GuiElement> getChildren()
    {
        return new ArrayList<>(children);
    }
    
    public final List<GuiElement> getChildrenDeep()
    {
        return getChildrenDeep0().collect(Collectors.toCollection(ArrayList::new));
    }
    
    private Stream<GuiElement> getChildrenDeep0()
    {
        return children.stream().flatMap(ch -> Stream.concat(Stream.of(ch), ch.getChildrenDeep0()));
    }
    
    @SuppressWarnings("unchecked")
    public final <T extends GuiElement> List<T> getChildrenByClass(Class<T> clazz)
    {
        return (List<T>) children.stream().filter(child -> clazz.isInstance(child))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    @SuppressWarnings("unchecked")
    public final <T extends GuiElement> List<T> getChildrenByClassDeep(Class<T> clazz)
    {
        return (List<T>) getChildrenDeep0().filter(child -> clazz.isInstance(child)).collect(Collectors.toCollection(ArrayList::new));
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
    
    public final void show()
    {
        setVisible(true);
    }
    
    public final void hide()
    {
        setVisible(false);
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        markDirty();
    }
    
    public boolean isVisible()
    {
        return visible;
    }
    
    protected void onElementAdd(GuiElement element)
    {
    }
    
    protected void onElementRemove(GuiElement element)
    {
    }
    
    public final void render(GuiCanvas canvas)
    {
        if ( visible )
        {
            render0(canvas);
        }
    }
    
    protected abstract void render0(GuiCanvas canvas);
    
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
