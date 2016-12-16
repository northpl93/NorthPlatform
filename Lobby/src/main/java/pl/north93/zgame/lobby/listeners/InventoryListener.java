package pl.north93.zgame.lobby.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.windows.WindowManager;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.lobby.LobbyFeatures;
import pl.north93.zgame.lobby.windows.MinigamePicker;
import pl.north93.zgame.lobby.windows.PetPicker;

public class InventoryListener implements Listener
{
    private BukkitApiCore apiCore;
    @InjectComponent("Lobby.Features")
    private LobbyFeatures component;

    public InventoryListener()
    {
        System.out.println("InventoryListener()");
    }

    @EventHandler
    public void giveOnJoin(final PlayerJoinEvent event)
    {
        if (this.component.getLobbyConfig().devMode)
        {
            event.getPlayer().sendMessage(ChatColor.RED + "Lobby uruchomione jest w DEV MODE! Menu na hotbarze wylaczone.");
            return;
        }
        final Inventory inventory = event.getPlayer().getInventory();

        final ItemStack minigameSelector = new ItemStack(Material.COMPASS, 1);
        final ItemMeta minigameSelectorMeta = minigameSelector.getItemMeta();
        minigameSelectorMeta.setDisplayName("Wybierz minigrę");
        minigameSelector.setItemMeta(minigameSelectorMeta);
        inventory.setItem(0, minigameSelector);

        final ItemStack petPicker = new ItemStack(Material.CHEST, 1);
        final ItemMeta petPickerMeta = petPicker.getItemMeta();
        petPickerMeta.setDisplayName("Wybierz zwierzaka");
        petPicker.setItemMeta(petPickerMeta);
        inventory.setItem(4, petPicker);
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event)
    {
        if (this.component.getLobbyConfig().devMode)
        {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event)
    {
        if (this.component.getLobbyConfig().devMode)
        {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event)
    {
        if (this.component.getLobbyConfig().devMode)
        {
            return;
        }
        event.setCancelled(true);
        final int slotNumber = event.getPlayer().getInventory().getHeldItemSlot();
        final WindowManager windowManager = this.apiCore.getWindowManager();

        switch (slotNumber)
        {
            case 0:
                windowManager.openWindow(event.getPlayer(), new MinigamePicker());
                break;
            case 4:
                windowManager.openWindow(event.getPlayer(), new PetPicker());
        }
    }
}

