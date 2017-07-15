package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

public class GuiContent
{
    private GuiContentEntry[][] content;
    
    private TranslatableString title;
    
    private GuiContentEntry getGuiContentEntry(int slotX, int slotY)
    {
        if ( slotX >= content.length || slotY >= content[0].length )
        {
            return null;
        }
        
        return content[slotX][slotY];
    }
    
    public GuiElement getGuiElementInSlot(int slotX, int slotY)
    {
        GuiContentEntry entry = getGuiContentEntry(slotX, slotY);
        return entry != null ? entry.element : null;
    }
    
    public GuiIcon getGuiIconInSlot(int slotX, int slotY)
    {
        GuiContentEntry entry = getGuiContentEntry(slotX, slotY);
        return entry != null ? entry.icon : null;
    }
    
    public void setEntry(int x, int y, GuiIcon icon, GuiElement element)
    {
        Preconditions.checkArgument(icon != null);
        Preconditions.checkArgument(element != null);
        
        if ( x < 0 || x >= content.length || y < 0 || y >= content[0].length )
        {
            return;
        }
        
        content[x][y] = new GuiContentEntry(icon, element);
    }
    
    public void renderToInventory(Player player, Vars<String> parameters)
    {
        Inventory inv = player.getOpenInventory().getTopInventory();
        Preconditions.checkState(inv.getType() == InventoryType.CHEST);
        
        if ( inv.getSize() != content.length * 9 )
        {
            inv = Bukkit.createInventory(null, content.length * 9, title.getValue(player.spigot().getLocale()));
            player.openInventory(inv);
        }
        
        inv.clear();
        
        for ( int i = 0; i < content.length; i++ )
        {
            for ( int j = 0; j < content[i].length; j++ )
            {
                inv.setItem(i * 9 + j, content[i][j].icon.toItemStack(player, parameters));
            }
        }
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