package pl.north93.northplatform.api.bukkit.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.north93.northplatform.api.bukkit.gui.ClickType;
import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.bukkit.gui.GuiCanvas;
import pl.north93.northplatform.api.bukkit.gui.HotbarEntry;
import pl.north93.northplatform.api.bukkit.gui.HotbarMenu;
import pl.north93.northplatform.api.bukkit.gui.IGuiManager;
import pl.north93.northplatform.api.bukkit.gui.element.GuiElement;
import pl.north93.northplatform.api.bukkit.player.event.PlayerPlatformLocaleChangedEvent;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.tick.ITickable;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.bukkit.tick.Tick;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northspigot.event.PlayerPressQEvent;

public class GuiTracker extends Component implements IGuiManager, ITickable, Listener
{
    private final Map<Player, GuiTrackerEntry> entriesByPlayer = new WeakHashMap<>();
    private final Multimap<Gui, GuiTrackerEntry> entriesByGui = ArrayListMultimap.create();
    private final Multimap<HotbarMenu, GuiTrackerEntry> entriesByHotbar = ArrayListMultimap.create();
    
    @Inject
    private IBukkitServerManager serverManager;
    @Inject
    private ITickableManager tickableManager;
    
    @Override
    protected void enableComponent()
    {
        serverManager.registerEvents(this);
        tickableManager.addTickableObject(this);
    }

    @Override
    protected void disableComponent()
    {
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
        return entriesByPlayer.computeIfAbsent(player, p -> new GuiTrackerEntry(this, p));
    }
    
    public Collection<GuiTrackerEntry> getEntries(Gui gui)
    {
        return entriesByGui.get(gui);
    }
    
    public Collection<GuiTrackerEntry> getEntries(HotbarMenu hotbarMenu)
    {
        return entriesByHotbar.get(hotbarMenu);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        GuiTrackerEntry entry = entriesByPlayer.remove(event.getPlayer());
        
        if ( entry != null && entry.getCurrentHotbarMenu() != null )
        {
            entry.closeHotbarMenu();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void closeHotBarWhenPlayerDies(final PlayerDeathEvent event)
    {
        final GuiTrackerEntry entry = this.getEntry(event.getEntity());
        if ( entry.getCurrentHotbarMenu() == null )
        {
            return;
        }

        final List<ItemStack> drops = event.getDrops();
        final PlayerInventory inventory = event.getEntity().getInventory();

        for (int i = 0; i < 9; i++)
        {
            // usuwamy z dropu wszystkie itemy będące na hotbarze
            drops.remove(inventory.getItem(i));
        }

        // dodajemy do dropu wszystkie przedmioty które gracz posiadał przed aktywowaniem hotbara
        drops.addAll(entry.getStoredHotBarItems());

        entry.closeHotbarMenu();
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
    public void disallowDropItemByQWhenHotBarIsOpened(final PlayerPressQEvent event)
    {
        GuiTrackerEntry entry = getEntry(event.getPlayer());
        if ( entry.getCurrentHotbarMenu() == null )
        {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void disallowHandSwapWhenHotBarIsOpened(final PlayerSwapHandItemsEvent event)
    {
        GuiTrackerEntry entry = getEntry(event.getPlayer());
        if ( entry.getCurrentHotbarMenu() == null )
        {
            return;
        }

        event.setCancelled(true);
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

    @EventHandler
    public void updateHotbarsOnLanguageChange(PlayerPlatformLocaleChangedEvent event)
    {
        final HotbarMenu hotbarMenu = this.getCurrentHotbarMenu(event.getPlayer());
        if (hotbarMenu == null)
        {
            return;
        }

        hotbarMenu.markDirty();
    }

    @Tick
    public void updateDirtyGuis()
    {
        final Map<Gui, Collection<GuiTrackerEntry>> map = new HashMap<>(this.entriesByGui.asMap());
        for (final Map.Entry<Gui, Collection<GuiTrackerEntry>> entry : map.entrySet())
        {
            final Gui gui = entry.getKey();
            if ( !gui.isDirty() )
            {
                continue;
            }

            gui.getContent().renderContent();
            gui.getContent().resetDirty();

            for ( final GuiTrackerEntry guiTrackerEntry : new ArrayList<>(entry.getValue()) )
            {
                guiTrackerEntry.refreshInventory();
            }
        }
    }

    @Tick
    public void updateDirtyHotbars()
    {
        final Map<HotbarMenu, Collection<GuiTrackerEntry>> map = new HashMap<>(this.entriesByHotbar.asMap());
        for (final Map.Entry<HotbarMenu, Collection<GuiTrackerEntry>> entry : map.entrySet())
        {
            final HotbarMenu hotbar = entry.getKey();
            if ( !hotbar.isDirty() )
            {
                continue;
            }

            hotbar.resetDirty();

            for ( final GuiTrackerEntry guiTrackerEntry : new ArrayList<>(this.entriesByHotbar.get(hotbar)) )
            {
                guiTrackerEntry.refreshHotbarMenu();
            }
        }
    }
}
