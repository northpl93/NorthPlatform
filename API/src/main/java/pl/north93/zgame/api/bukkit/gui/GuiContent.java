package pl.north93.zgame.api.bukkit.gui;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

public class GuiContent extends GuiContainerElement
{
    private final Gui gui;
    
    private TranslatableString title = TranslatableString.empty();
    
    private GuiCanvas renderedCanvas;
    
    public GuiContent(Gui gui, int height)
    {
        super(9, height);
        this.gui = gui;
        this.renderedCanvas = new GuiCanvas(9, height);
    }
    
    public GuiCanvas getRenderedCanvas()
    {
        return renderedCanvas;
    }
    
    public TranslatableString getTitle()
    {
        return title;
    }
    
    public void setTitle(TranslatableString title)
    {
        Preconditions.checkArgument(title != null, "Title cannot be null");
        
        this.title = title;
        markDirty();
    }
    
    public void setHeight(int height)
    {
        setSize(9, height);
        renderedCanvas.resize(9, height);
    }
    
    public void renderContent()
    {
        renderedCanvas.clear();
        this.gui.callOnRender();
        render(renderedCanvas);
    }
    
    public void renderToInventory(Player player, Inventory inv)
    {
        Preconditions.checkState(inv.getType() == InventoryType.CHEST);
        inv.clear();
        
        for ( int i = 0; i < renderedCanvas.getWidth(); i++ )
        {
            for ( int j = 0; j < renderedCanvas.getHeight(); j++ )
            {
                IGuiIcon icon = renderedCanvas.getGuiIconInSlot(i, j);
                GuiElement element = renderedCanvas.getGuiElementInSlot(i, j);
                if ( icon != null )
                {
                    final Vars<Object> vars = element.getVariables();
                    inv.setItem(j * 9 + i, icon.toItemStack(gui.getMessagesBox(), player, vars));
                }
            }
        }
    }
    
    @Override
    public void setSize(int width, int height)
    {
        Preconditions.checkArgument(width == 9, "Content width must be equal 9");
        
        super.setSize(width, height);
    }
}
