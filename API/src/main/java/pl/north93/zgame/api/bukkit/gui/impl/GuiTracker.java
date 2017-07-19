package pl.north93.zgame.api.bukkit.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.gui.ClickType;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiCanvas;
import pl.north93.zgame.api.bukkit.gui.GuiElement;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class GuiTracker extends Component implements IGuiManager, ITickable, Listener
{
    private final ClickHandlerManager clickHandlerManager = new ClickHandlerManager();
    
    private final Map<Player, GuiTrackerEntry> entriesByPlayer = new WeakHashMap<>();
    private final Multimap<Gui, GuiTrackerEntry> entriesByGui = ArrayListMultimap.create();
    
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
    }

    @Override
    protected void disableComponent()
    {

    }
    
    public ClickHandlerManager getClickHandlerManager()
    {
        return clickHandlerManager;
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
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Gui> T getCurrentGui(Player player)
    {
        return (T) Optional.ofNullable(getEntry(player)).map(entry -> entry.getCurrentGui()).orElse(null);
    }
    
    public GuiTrackerEntry getEntry(Player player)
    {
        return entriesByPlayer.get(player);
    }
    
    public Collection<GuiTrackerEntry> getEntries(Gui gui)
    {
        return entriesByGui.get(gui);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        entriesByPlayer.put(event.getPlayer(), new GuiTrackerEntry(this, event.getPlayer()));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        System.out.println("Quit event");
        entriesByPlayer.remove(event.getPlayer());
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        System.out.println("Inventory close");
        
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
        
        if ( entry.getCurrentGui() == null || event.getClickedInventory() == null )
        {
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
    public void onChat(PlayerCommandPreprocessEvent event)
    {
        if ( event.getMessage().equals("/examplegui") )
        {
            event.setCancelled(true);
            
            final TestGui test = new TestGui();
            
            test.open(event.getPlayer());
            event.getPlayer().sendMessage("Gui should open now");
        }
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
}
