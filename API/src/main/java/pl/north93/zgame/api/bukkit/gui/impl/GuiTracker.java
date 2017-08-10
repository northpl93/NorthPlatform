package pl.north93.zgame.api.bukkit.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.gui.ClickType;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiCanvas;
import pl.north93.zgame.api.bukkit.gui.GuiClickEvent;
import pl.north93.zgame.api.bukkit.gui.GuiElement;
import pl.north93.zgame.api.bukkit.gui.HotbarEntry;
import pl.north93.zgame.api.bukkit.gui.HotbarMenu;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class GuiTracker extends Component implements IGuiManager, ITickable, Listener
{
    private final ClickHandlerManager<GuiClickEvent> guiClickHandlerManager = new ClickHandlerManager<>(GuiClickEvent.class);
    private final ClickHandlerManager<HotbarClickEvent> hotbarClickHandlerManager = new ClickHandlerManager<>(HotbarClickEvent.class);
    
    private final Map<Player, GuiTrackerEntry> entriesByPlayer = new WeakHashMap<>();
    private final Multimap<Gui, GuiTrackerEntry> entriesByGui = ArrayListMultimap.create();
    private final Multimap<HotbarMenu, GuiTrackerEntry> entriesByHotbar = ArrayListMultimap.create();
    
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private ITickableManager tickableManager;
    
    @Override
    protected void enableComponent()
    {
        apiCore.registerEvents(this);
        tickableManager.addTickableObject(this);
        
        // Statyczny @Inject nie dziala prawidlowo, wiec trzeba tak wstrzyknac instancje :(
        Gui.setGuiTracker(this);
        HotbarMenu.setGuiTracker(this);
    }

    @Override
    protected void disableComponent()
    {

    }
    
    public ClickHandlerManager<GuiClickEvent> getGuiClickHandlerManager()
    {
        return guiClickHandlerManager;
    }
    
    public ClickHandlerManager<HotbarClickEvent> getHotbarClickHandlerManager()
    {
        return hotbarClickHandlerManager;
    }
    
    @Override
    public void openGui(Player player, Gui gui)
    {
        if ( !entriesByGui.containsKey(gui) )
        {
            // renderujemy gui jeżeli nie zostało otwarte po raz pierwszy
            gui.getContent().renderContent();
        }
        
        GuiTrackerEntry entry = getEntry(player);
        entry.openNewGui(gui);
        gui.callOnOpen(player);
    }

    void addGuiViewer(Gui gui, GuiTrackerEntry viewer)
    {
        entriesByGui.put(gui, viewer);
    }
    
    void addHotbarViewer(HotbarMenu hotbar, GuiTrackerEntry viewer)
    {
        entriesByHotbar.put(hotbar, viewer);
    }
    
    void removeHotbarViewer(HotbarMenu hotbar, GuiTrackerEntry viewer)
    {
        entriesByHotbar.remove(hotbar, viewer);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Gui> T getCurrentGui(Player player)
    {
        return (T) Optional.ofNullable(getEntry(player)).map(entry -> entry.getCurrentGui()).orElse(null);
    }
    
    @Override
    public void displayHotbarMenu(Player player, HotbarMenu hotbarMenu)
    {
        GuiTrackerEntry entry = getEntry(player);
        entry.openNewHotbarMenu(hotbarMenu);
    }
    
    @Override
    public void closeHotbarMenu(Player player)
    {
        GuiTrackerEntry entry = getEntry(player);
        entry.closeHotbarMenu();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends HotbarMenu> T getCurrentHotbarMenu(Player player)
    {
        GuiTrackerEntry entry = getEntry(player);
        return (T) entry.getCurrentHotbarMenu();
    }
    
    public GuiTrackerEntry getEntry(Player player)
    {
        return entriesByPlayer.get(player);
    }
    
    public Collection<GuiTrackerEntry> getEntries(Gui gui)
    {
        return entriesByGui.get(gui);
    }
    
    public Collection<GuiTrackerEntry> getEntries(HotbarMenu hotbarMenu)
    {
        return entriesByHotbar.get(hotbarMenu);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        entriesByPlayer.put(event.getPlayer(), new GuiTrackerEntry(this, event.getPlayer()));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        GuiTrackerEntry entry = entriesByPlayer.remove(event.getPlayer());
        
        if ( entry.getCurrentHotbarMenu() != null )
        {
            entry.closeHotbarMenu();
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        GuiTrackerEntry entry = getEntry((Player) event.getPlayer());
        if ( entry.getCurrentGui() == null )
        {
            return;
        }
        
        entriesByGui.remove(entry.getCurrentGui(), entry);
        entry.onCloseInventory();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        GuiTrackerEntry entry = getEntry((Player) event.getWhoClicked());
        
        if ( event.getClickedInventory() == null )
        {
            // jak kliknieto poza ekwipunkiem to nic nie robimy
            return;
        }

        if ( entry.getCurrentHotbarMenu() != null && event.getSlotType() == InventoryType.SlotType.QUICKBAR )
        {
            // blokujemy klikniecia na hotbar gdy mamy jakis aktywny
            event.setCancelled(true);
            return;
        }

        if ( entry.getCurrentGui() == null )
        {
            // jak brak otwartego gui to nic nie musimy robic
            return;
        }
        
        event.setCancelled(true);
        
        if ( !event.isRightClick() && !event.isLeftClick() )
        {
            return;
        }
        
        GuiCanvas canvas = entry.getCurrentGui().getContent().getRenderedCanvas();
        
        int slot = event.getRawSlot();
        int x = slot % 9;
        int y = slot / 9;
        
        GuiElement clickedElement = canvas.getGuiElementInSlot(x, y);
        if ( clickedElement != null )
        {
            entry.getCurrentGui().click(entry.getPlayer(), clickedElement, ClickType.fromBukkitClickType(event.getClick()));
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if ( event.getAction() == Action.PHYSICAL )
        {
            return;
        }
        
        GuiTrackerEntry entry = getEntry(event.getPlayer());
        if ( entry.getCurrentHotbarMenu() == null )
        {
            return;
        }
        
        event.setUseItemInHand(Result.DENY);
        
        int slot = event.getPlayer().getInventory().getHeldItemSlot();
        
        HotbarEntry clickedEntry = entry.getCurrentHotbarMenu().getEntry(slot);
        entry.getCurrentHotbarMenu().click(event.getPlayer(), clickedEntry, ClickType.fromBukkitAction(event.getAction()));
    }
    
    @Tick
    public void updateDirtyGuis()
    {
        for ( Gui gui : entriesByGui.keySet() )
        {
            if ( !gui.isDirty() )
            {
                continue;
            }
            
            gui.getContent().renderContent();
            gui.getContent().resetDirty();
            
            for ( GuiTrackerEntry entry : new ArrayList<>(entriesByGui.get(gui)) )
            {
                entry.refreshInventory();
            }
        }
    }
    
    @Tick
    public void updateDirtyHotbars()
    {
        for ( HotbarMenu hotbar : entriesByHotbar.keySet() )
        {
            if ( !hotbar.isDirty() )
            {
                continue;
            }
            
            hotbar.resetDirty();
            
            for ( GuiTrackerEntry entry : entriesByHotbar.get(hotbar) )
            {
                entry.refreshHotbarMenu();
            }
        }
    }
}
