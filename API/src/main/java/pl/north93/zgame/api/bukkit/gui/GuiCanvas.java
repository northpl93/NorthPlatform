package pl.north93.zgame.api.bukkit.gui;

import java.util.Optional;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

public class GuiCanvas
{
    private GuiContentEntry[][] content;
    
    private int width;
    private int height;
    
    public GuiCanvas(int width, int height)
    {
        checkWidthAndHeight(width, height);
        this.content = new GuiContentEntry[width][height];
        
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
    
    public void clear()
    {
        IntStream.range(0, content.length).forEach(i -> IntStream.range(0, content[i].length).forEach(j -> { content[i][j] = null; } ));
    }
    
    public void resize(int newWidth, int newHeight)
    {
        checkWidthAndHeight(newWidth, newHeight);
        
        GuiContentEntry[][] newContent = new GuiContentEntry[newWidth][newHeight];
        
        IntStream.range(0, Math.min(content.length, newContent.length))
                .forEach(i -> System.arraycopy(content[i], 0, newContent[i], 0, Math.min(content[i].length, newContent[i].length)));
        
        width = newWidth;
        height = newHeight;
        content = newContent;
    }
    
    public void paste(int x, int y, GuiCanvas other, boolean skipNull)
    {
        for ( int i = 0; i < other.getWidth(); i++ )
        {
            for ( int j = 0; j < other.getHeight(); j++ )
            {
                Optional<GuiContentEntry> entry = other.getGuiContentEntry(i, j);
                if ( !skipNull || entry.isPresent() )
                {
                    setEntry(x + i, y + j, entry.orElse(null));
                }
            }
        }
    }
    
    public GuiElement getGuiElementInSlot(int slotX, int slotY)
    {
        return getGuiContentEntry(slotX, slotY).map(e -> e.element).orElse(null);
    }
    
    public GuiIcon getGuiIconInSlot(int slotX, int slotY)
    {
        return getGuiContentEntry(slotX, slotY).map(e -> e.icon).orElse(null);
    }
    
    public void setEntry(int x, int y, GuiIcon icon, GuiElement element)
    {
        Preconditions.checkArgument(icon != null);
        Preconditions.checkArgument(element != null);
        
        setEntry(x, y, new GuiContentEntry(icon, element));
    }
       
    private void setEntry(int x, int y, GuiContentEntry entry)
    {
        if ( x < 0 || x >= getWidth() || y < 0 || y >= getHeight() )
        {
            return;
        }
        
        content[x][y] = entry;
    }
    
    private Optional<GuiContentEntry> getGuiContentEntry(int slotX, int slotY)
    {
        if ( slotX >= content.length || slotY >= content[0].length )
        {
            return Optional.empty();
        }
            
        return Optional.ofNullable(content[slotX][slotY]);
    }
    
    private void checkWidthAndHeight(int width, int height) throws IllegalArgumentException
    {
        Preconditions.checkArgument(width > 0, "Width must be greater than 0");
        Preconditions.checkArgument(height > 0, "Height must be greater than 0");
    }
}

class GuiContentEntry
{
    final GuiIcon icon;
    final GuiElement element;
    
    GuiContentEntry(GuiIcon icon, GuiElement element)
    {
        this.icon = icon;
        this.element = element;
    }
}