package pl.north93.zgame.api.bukkit.gui.impl;

import java.util.Optional;

import net.minecraft.server.v1_12_R1.EntityPlayer;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiContent;
import pl.north93.zgame.api.bukkit.gui.HotbarEntry;
import pl.north93.zgame.api.bukkit.gui.HotbarMenu;

public class GuiTrackerEntry
{
    private final GuiTracker guiTracker;
    private final Player player;
    
    private Gui currentGui;
    private Inventory currentInventory;
    
    private HotbarMenu currentHotbarMenu;
    private final ItemStack[] storedHotbarItems = new ItemStack[9];
    
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
    
    public HotbarMenu getCurrentHotbarMenu()
    {
        return currentHotbarMenu;
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
        this.cleanupOldInventory();
        player.openInventory(inv);
        
        currentGui = newGui;
        currentInventory = inv;
        guiTracker.addGuiViewer(currentGui, this);
    }

    /**
     * Gdy otwieramy GUI przez openInventory, a gracz ma juz otwarte inne GUI to
     * bukkit dziwnie to obsluguje i udaje odebranie pakietu zamknięcia okna.
     * Gdy nastąpi to w wątku innym niż wątek serwera to wyleci wyjątek CancelledPacketHandleException.
     *
     * Omijamy to ręcznie zamykając poprzednie okno po stronie serwera i oszukując tym samym Bukkita
     * przy tworzeniu nowego inventory.
     */
    private void cleanupOldInventory()
    {
        final CraftPlayer craftPlayer = (CraftPlayer) this.player;
        final EntityPlayer entityPlayer = craftPlayer.getHandle();

        final CraftInventoryView craftInventoryView = (CraftInventoryView) craftPlayer.getOpenInventory();
        if (craftInventoryView.getHandle() == entityPlayer.defaultContainer)
        {
            // gracz nie ma otwartego gui lub ma otwarty swój ekwipunek
            return;
        }

        CraftEventFactory.handleInventoryCloseEvent(entityPlayer);
        entityPlayer.r(); // skopiowane z EntityPlayer#closeInventory()
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
    
    public void openNewHotbarMenu(HotbarMenu newMenu)
    {
        if ( currentHotbarMenu != null )
        {
            closeHotbarMenu();
        }
        
        storeHotbarItems();
        
        this.currentHotbarMenu = newMenu;
        
        refreshHotbarMenu();
        guiTracker.addHotbarViewer(newMenu, this);
        this.currentHotbarMenu.callOnOpen(player);
    }
    
    public void refreshHotbarMenu()
    {
        for ( HotbarEntry entry : currentHotbarMenu.getEntries() )
        {
            if ( !entry.isVisible() )
            {
                continue;
            }
            
            ItemStack is = Optional.ofNullable(entry.getIcon()).map(icon -> icon.toItemStack(currentHotbarMenu.getMessages(), player, currentHotbarMenu.getVariables())).orElse(null);
            player.getInventory().setItem(entry.getSlot(), is);
        }
        
        player.updateInventory();
    }
    
    public void closeHotbarMenu()
    {
        currentHotbarMenu.callOnClose(player);
        guiTracker.removeHotbarViewer(currentHotbarMenu, this);
        currentHotbarMenu = null;
        restoreHotbarItems();
    }
    
    private void storeHotbarItems()
    {
        PlayerInventory inv = player.getInventory();
        for ( int i = 0; i < storedHotbarItems.length; i++ )
        {
            storedHotbarItems[i] = inv.getItem(i);
        }
    }
    
    private void restoreHotbarItems()
    {
        PlayerInventory inv = player.getInventory();
        for ( int i = 0; i < storedHotbarItems.length; i++ )
        {
            inv.setItem(i, storedHotbarItems[i]);
            storedHotbarItems[i] = null;
        }
    }
}
