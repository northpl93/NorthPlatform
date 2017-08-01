package pl.arieals.minigame.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import pl.arieals.minigame.bedwars.shop.ShopManager;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageEntry;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageTracker;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.PluralForm;

public class PlayerItemsListener implements Listener
{
    @Inject
    private ShopManager shopManager;
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event)
    {
        final InventoryType.SlotType slotType = event.getSlotType();
        if (slotType == InventoryType.SlotType.ARMOR || slotType == InventoryType.SlotType.CRAFTING)
        {
            event.setCancelled(true);
            return;
        }

        final Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getType() == InventoryType.CHEST)
        {
            final ItemStack cursor = event.getCursor();
            if (cursor != null && this.shopManager.isItemPermanent(cursor))
            {
                // blokujemy wywalanie itemow stalych do skrzynki.
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockDropPermanentItem(final PlayerDropItemEvent event)
    {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        if (this.shopManager.isItemPermanent(itemStack))
        {
            // blokujemy wywalanie itemow stalych z ekwipunku
            event.setCancelled(true);
        }
    }

    /*
     * Zbroja zostaje
     * Surowce do zabojcy
     * Reszta przepada o ile nie trwala.
     */
    @EventHandler
    public void filterDeathItems(final PlayerDeathEvent event)
    {
        event.setKeepInventory(true);

        final Player player = event.getEntity();
        final ItemStack[] storageContents = player.getInventory().getStorageContents();

        final Object2IntMap<Material> trackedMaterials = new Object2IntOpenHashMap<>(5, 0.01f);
        for (int i = 0; i < storageContents.length; i++)
        {
            final ItemStack item = storageContents[i];
            if (item == null || item.getType() == Material.AIR)
            {
                continue;
            }
            if (this.shopManager.isItemPermanent(item))
            {
                continue;
            }

            this.addTrackedMaterial(trackedMaterials, item);
            player.getInventory().setItem(i, null);
        }
        this.giveItemsToKiller(trackedMaterials, player);
    }

    // dodaje sledzony material do mapy
    private void addTrackedMaterial(final Object2IntMap<Material> map, final ItemStack itemStack)
    {
        final Material type = itemStack.getType();
        if (type != Material.IRON_INGOT && type != Material.GOLD_INGOT && type != Material.DIAMOND && type != Material.EMERALD && !(type == Material.INK_SACK && itemStack.getDurability() == 4))
        {
            return;
        }

        final int anInt = map.getInt(type);
        map.put(type, anInt + itemStack.getAmount());
    }

    // daje przedmioty z mapy zabojcy danego gracza
    private void giveItemsToKiller(final Object2IntMap<Material> items, final Player death)
    {
        final DamageEntry lastDamageByPlayer = DamageTracker.get().getContainer(death).getLastDamageByPlayer();
        if (lastDamageByPlayer == null)
        {
            return;
        }
        final Player lastDamager = lastDamageByPlayer.getPlayerDamager();
        assert lastDamager != null;

        for (final Object2IntMap.Entry<Material> entry : items.object2IntEntrySet())
        {
            final Material type = entry.getKey();
            int amount = entry.getIntValue();
            while (amount > 0)
            {
                final ItemStack itemStack = new ItemStack(type, Math.min(amount, 64), (byte)(type == Material.INK_SACK ? 4 : 0));
                lastDamager.getInventory().addItem(itemStack);
                amount -= 64;
            }

            final String messageKey = PluralForm.transformKey("die.received_items." + type, entry.getIntValue());
            this.messages.sendMessage(lastDamager, messageKey, entry.getIntValue());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
