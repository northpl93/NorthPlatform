package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.time.Duration;
import java.util.ListIterator;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.cursors.ObjectIntCursor;

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
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.event.PlayerRevivedEvent;
import pl.arieals.minigame.bedwars.shop.ShopManager;
import pl.arieals.minigame.bedwars.shop.UpgradeManager;
import pl.arieals.minigame.bedwars.shop.stattrack.StatTrackItems;
import pl.arieals.minigame.bedwars.shop.upgrade.RoadOfWarrior;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageEntry;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageTracker;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.PluralForm;

public class PlayerItemsListener implements Listener
{
    @Inject
    private ShopManager    shopManager;
    @Inject
    private UpgradeManager upgradeManager;
    @Inject
    private StatTrackItems statTrackItems;
    @Inject @Messages("BedWars")
    private MessagesBox    messages;

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
        final Player player = event.getEntity();
        event.setKeepInventory(true);

        final ObjectIntMap<Material> trackedMaterials = new ObjectIntHashMap<>(5, 0.01f);

        final ListIterator<ItemStack> iterator = player.getInventory().iterator();
        while (iterator.hasNext())
        {
            final ItemStack item = iterator.next();
            if (item == null || item.getType() == Material.AIR)
            {
                continue;
            }

            final int index = iterator.nextIndex() - 1;
            if (index >= 36 && index <= 39)
            {
                // zbroja nas nie interesuje
                continue;
            }

            if (this.shopManager.isItemPermanent(item))
            {
                continue;
            }

            this.addTrackedMaterial(trackedMaterials, item);
            player.getInventory().setItem(index, null);
        }

        this.giveItemsToKiller(trackedMaterials, player);
    }

    // dodaje sledzony material do mapy
    private void addTrackedMaterial(final ObjectIntMap<Material> map, final ItemStack itemStack)
    {
        final Material type = itemStack.getType();
        if (type != Material.IRON_INGOT && type != Material.GOLD_INGOT && type != Material.DIAMOND && type != Material.EMERALD && !(type == Material.INK_SACK && itemStack.getDurability() == 4))
        {
            return;
        }

        final int anInt = map.get(type);
        map.put(type, anInt + itemStack.getAmount());
    }

    // daje przedmioty z mapy zabojcy danego gracza
    private void giveItemsToKiller(final ObjectIntMap<Material> items, final Player death)
    {
        final DamageEntry lastDamageByPlayer = DamageTracker.get().getContainer(death).getLastDamageByPlayer(Duration.ofSeconds(10));
        if (lastDamageByPlayer == null)
        {
            return;
        }
        final Player lastDamager = lastDamageByPlayer.getPlayerDamager();
        assert lastDamager != null;

        final BedWarsPlayer playerData = getPlayerData(lastDamager, BedWarsPlayer.class);
        if (playerData == null || playerData.isEliminated())
        {
            // wyeliminowanemu graczowi nie dajemy surowcow.
            return;
        }

        for (final ObjectIntCursor<Material> entry : items)
        {
            final Material type = entry.key;
            int amount = entry.value;
            while (amount > 0)
            {
                final ItemStack itemStack = new ItemStack(type, Math.min(amount, 64), (byte)(type == Material.INK_SACK ? 4 : 0));
                lastDamager.getInventory().addItem(itemStack);
                amount -= 64;
            }

            final String messageKey = PluralForm.transformKey("die.received_items." + type, entry.value);
            this.messages.sendMessage(lastDamager, messageKey, entry.value);
        }
    }

    @EventHandler // dodajemy drewniany miecz gdy gra startuje
    public void giveWoodSwordWhenGameStarts(final GameStartEvent event)
    {
        for (final Player player : event.getArena().getPlayersManager().getPlayers())
        {
            this.giveWoodSword(player);
        }
    }

    @EventHandler // dodajemy drewniany miecz gdy gracz sie respawni
    public void giveWoodSwordWhenPlayerRespawn(final PlayerRevivedEvent event)
    {
        final Player bukkitPlayer = event.getBedWarsPlayer().getBukkitPlayer();
        this.giveWoodSword(bukkitPlayer);
    }

    /**
     * Daje graczowi podanemu w argumencie startowy drewniany miecz.
     * @param player gracz ktoremu dac miecz.
     */
    private void giveWoodSword(final Player player)
    {
        final ItemStack woodSword = new ItemStack(Material.WOOD_SWORD);
        final ItemMeta itemMeta = woodSword.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        woodSword.setItemMeta(itemMeta);

        final RoadOfWarrior upgrade = (RoadOfWarrior) this.upgradeManager.getUpgradeByName("RoadOfWarrior");
        if (upgrade != null)
        {
            final int upgradeLevel = upgrade.getUpgradeLevel(player);
            if (upgradeLevel > 0)
            {
                upgrade.apply(woodSword, upgradeLevel);
            }
        }

        // updatujemy stat tracka na drewnianym mieczu
        this.statTrackItems.updateItem(player, woodSword);

        player.getInventory().addItem(woodSword); // drewniany miecz na start
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
