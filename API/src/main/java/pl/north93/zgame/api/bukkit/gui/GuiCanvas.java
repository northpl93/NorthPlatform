package pl.north93.zgame.api.bukkit.gui;

import java.util.Optional;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

public class GuiCanvas
{
    private GuiContentEntry[][] content;
    
    private int width;
    private int height;
    private int offsetY;
    
    public GuiCanvas(int width, int height, int offsetY)
    {
        checkWidthAndHeight(width, height);
        this.content = new GuiContentEntry[width][height];
        
        this.width = width;
        this.height = height;
        this.offsetY = offsetY;
    }

    public GuiCanvas(final int width, final int height)
    {
        this(width, height, 0);
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

    public void changeOffset(int newYOffset)
    {
        this.offsetY = newYOffset;

        // todo przekopiowac odpowiednie wiersze
        this.content = new GuiContentEntry[this.width][this.height];
    }
    
    public void paste(int x, int y, GuiCanvas other, boolean skipNull)
    {
        for ( int i = 0; i < other.getWidth(); i++ )
        {
            for ( int j = 0; j < other.getHeight(); j++ )
            {
                Optional<GuiContentEntry> entry = other.getGuiContentEntry(i, j + other.offsetY);
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
    
    public IGuiIcon getGuiIconInSlot(int slotX, int slotY)
    {
        return getGuiContentEntry(slotX, slotY).map(e -> e.icon).orElse(null);
    }
    
    public void setEntry(int x, int y, IGuiIcon icon, GuiElement element)
    {
        Preconditions.checkArgument(icon != null);
        Preconditions.checkArgument(element != null);
        
        setEntry(x, y, new GuiContentEntry(icon, element));
    }
       
    private void setEntry(int x, int y, GuiContentEntry entry)
    {
        if ( this.isInvalidSlot(x, y) )
        {
            return;
        }
        
        content[x][y - this.offsetY] = entry;
    }
    
    private Optional<GuiContentEntry> getGuiContentEntry(int slotX, int slotY)
    {
        if ( this.isInvalidSlot(slotX, slotY) )
        {
            return Optional.empty();
        }
            
        return Optional.ofNullable(content[slotX][slotY - this.offsetY]);
    }

    // zwróci true jeśli dany slot znajduje się poza tym canvasem
    private boolean isInvalidSlot(final int x, final int y)
    {
        return x < 0 || x >= getWidth() || y - this.offsetY < 0 || y - this.offsetY >= getHeight();
    }

    private void checkWidthAndHeight(int width, int height) throws IllegalArgumentException
    {
        Preconditions.checkArgument(width > 0, "Width must be greater than 0");
        Preconditions.checkArgument(height > 0, "Height must be greater than 0");
    }
}

class GuiContentEntry
{
    final IGuiIcon   icon;
    final GuiElement element;
    
    GuiContentEntry(IGuiIcon icon, GuiElement element)
    {
        this.icon = icon;
        this.element = element;
    }
}