package pl.north93.zgame.api.bukkit.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiContent;

public class GuiTrackerEntry
{
    private final GuiTracker guiTracker;
    private final Player player;
    
    private Gui currentGui;
    private Inventory currentInventory;
    
    public GuiTrackerEntry(GuiTracker guiTracker, Player player)
    {
        this.guiTracker = guiTracker;
        this.player = player;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public Gui getCurrentGui()
    {
        return currentGui;
    }
    
    public Inventory getCurrentInventory()
    {
        return currentInventory;
    }
    
    public void openNewGui(Gui newGui)
    {
        GuiContent content = newGui.getContent();
        
        Inventory inv = Bukkit.createInventory(null, 9 * content.getHeight(), content.getTitle().getValue(player, newGui.getVariables()));
        content.renderToInventory(player, inv);
        player.openInventory(inv);
        
        currentGui = newGui;
        currentInventory = inv;
        guiTracker.addGuiViewer(currentGui, this);
    }
    
    public void refreshInventory()
    {
        GuiContent content = currentGui.getContent();
        
        Inventory inv = player.getOpenInventory().getTopInventory();
        Preconditions.checkState(inv.equals(currentInventory));
        
        String title = content.getTitle().getValue(player, currentGui.getVariables());
        
        if ( inv.getSize() != content.getHeight() * content.getWidht() || !inv.getTitle().equals(title) )
        {
            // we need to open another inv when gui size or title change
            openNewGui(currentGui);
            return;
        }
        
        content.renderToInventory(player, inv);
        player.updateInventory();
    }
    
    public void onCloseInventory()
    {
        currentGui.callOnClose(player);
        currentGui = null;
        currentInventory = null;
        
    }
}
