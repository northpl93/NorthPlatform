package pl.north93.northplatform.api.bukkit.gui.impl;

import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsPlayer;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.server.v1_12_R1.ContainerChest;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenWindow;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.bukkit.gui.HotbarEntry;
import pl.north93.northplatform.api.bukkit.gui.HotbarMenu;
import pl.north93.northplatform.api.bukkit.gui.element.GuiContent;
import pl.north93.northplatform.api.bukkit.gui.event.GuiOpenEvent;
import pl.north93.northplatform.api.global.messages.LegacyMessage;

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

    private EntityPlayer getEntityPlayer()
    {
        return toNmsPlayer(this.player);
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

        final LegacyMessage guiName = content.getTitle().getLegacy(player.getLocale(), newGui.getVariables());

        Inventory inv = Bukkit.createInventory(null, 9 * content.getHeight(), guiName.asString());
        content.renderToInventory(player, inv);

        if ( tryOpenContainerAndCallOwnEvent(inv, newGui) )
        {
            currentGui = newGui;
            currentInventory = inv;
            guiTracker.addGuiViewer(currentGui, this);
        }
    }
    
    // Below is rewrite of NMS method EntityPlayer#openContainer and CraftBukkit method CraftEventFactory#callInventoryOpenEvent
    // We call own GuiOpenEvent instead Bukkit's InventoryOpenEvent
    private boolean tryOpenContainerAndCallOwnEvent(Inventory inv, Gui newGui)
    {
        Preconditions.checkState(inv.getType() == InventoryType.CHEST);
        CraftInventoryCustom craftInv = (CraftInventoryCustom) inv;
        EntityPlayer entityPlayer = getEntityPlayer();
        
        ContainerChest container = new ContainerChest(entityPlayer.inventory, craftInv.getInventory(), entityPlayer);
        if ( callGuiOpenEvent(container, newGui).isCancelled() )
        {
            craftInv.getInventory().closeContainer(entityPlayer);
            return false;
        }
        
        closeContainerIfAnyOpenOnServerSide();
        Preconditions.checkState(entityPlayer.defaultContainer == entityPlayer.activeContainer);
        openContainer(container);
        
        return true;
    }
    
    private void openContainer(ContainerChest container)
    {
        EntityPlayer entityPlayer = getEntityPlayer();
        IInventory topInventory = container.e();
        
        int windowId = entityPlayer.nextContainerCounter();
        entityPlayer.activeContainer = container;
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(windowId, "minecraft:container", topInventory.getScoreboardDisplayName(), topInventory.getSize()));
        container.windowId = windowId;
        container.addSlotListener(entityPlayer);
    }
    
    private void closeContainerIfAnyOpenOnServerSide()
    {
        EntityPlayer entityPlayer = getEntityPlayer();
        
        if ( entityPlayer.activeContainer != entityPlayer.defaultContainer )
        {
            CraftEventFactory.handleInventoryCloseEvent(entityPlayer);
            entityPlayer.r();
        }
    }
    
    private GuiOpenEvent callGuiOpenEvent(ContainerChest container, Gui newGui)
    {
        EntityPlayer entityPlayer = getEntityPlayer();
        entityPlayer.activeContainer.transferTo(container, entityPlayer.getBukkitEntity());
        
        GuiOpenEvent event = new GuiOpenEvent(container.getBukkitView(), newGui);
        Bukkit.getPluginManager().callEvent(event);
        if ( event.isCancelled() )
        {
            container.transferTo(entityPlayer.activeContainer, entityPlayer.getBukkitEntity());
        }
        
        return event;
    }
    
    public void refreshInventory()
    {
        GuiContent content = currentGui.getContent();
        
        Inventory inv = player.getOpenInventory().getTopInventory();
        Preconditions.checkState(inv.equals(currentInventory));
        
        String title = content.getTitle().getLegacy(player.getLocale(), currentGui.getVariables()).asString();
        
        if ( inv.getSize() != content.getHeight() * content.getWidth() || !inv.getTitle().equals(title) )
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

    /*default*/ List<ItemStack> getStoredHotBarItems()
    {
        if (this.currentHotbarMenu == null)
        {
            return Collections.emptyList();
        }
        return Arrays.asList(this.storedHotbarItems);
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
