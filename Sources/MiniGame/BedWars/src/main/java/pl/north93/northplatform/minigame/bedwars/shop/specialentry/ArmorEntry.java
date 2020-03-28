package pl.north93.northplatform.minigame.bedwars.shop.specialentry;

import static java.text.MessageFormat.format;


import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.minigame.bedwars.event.ItemPreBuyEvent;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ArmorMaterial;

@Slf4j
public class ArmorEntry implements IShopSpecialEntry, Listener
{
    private ArmorEntry(final BukkitApiCore apiCore) // aggregator wspiera SmartExecutora, wiec wstrzykiwanie dziala
    {
        apiCore.registerEvents(this);
    }

    @Override
    public boolean buy(final INorthPlayer player, final Collection<ItemStack> items)
    {
        final ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (final ItemStack item : items)
        {
            final ArmorType type = ArmorType.getType(item.getType());
            armorContents[type.ordinal()] = item;

            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            item.setItemMeta(itemMeta);
        }
        player.getInventory().setArmorContents(armorContents);
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemBuy(final ItemPreBuyEvent event)
    {
        if (! "ArmorEntry".equals(event.getShopEntry().getSpecialHandler()))
        {
            return;
        }

        final PlayerInventory inventory = event.getPlayer().getInventory();

        final ItemStack itemStack = inventory.getArmorContents()[0];
        if (itemStack == null)
        {
            return;
        }

        final ArmorMaterial hasType = ArmorMaterial.getArmorMaterial(itemStack.getType());
        final ArmorMaterial buyingType = ArmorMaterial.getArmorMaterial(Material.getMaterial(event.getShopEntry().getItems().get(0).getId()));

        if (buyingType.isBetterOrSame(hasType))
        {
            log.info("Canceled purchase because player already has this armor, arena {}, player {}, armor {}",
                    event.getArena().getId(), event.getPlayer().getName(), event.getShopEntry().getInternalName());
            event.setBuyStatus(ItemPreBuyEvent.BuyStatus.ALREADY_HAVE);
        }
    }
}

/**
 * Typ armoru wedlug kolejnosci z {@link PlayerInventory#getArmorContents()}
 */
enum ArmorType
{
    BOOTS,
    LEGGINGS,
    CHEST_PLATE,
    HELMET;

    static ArmorType getType(final Material material)
    {
        switch (material)
        {
            case LEATHER_HELMET:
            case IRON_HELMET:
            case GOLD_HELMET:
            case CHAINMAIL_HELMET:
            case DIAMOND_HELMET:
                return HELMET;

            case LEATHER_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                return CHEST_PLATE;

            case LEATHER_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLD_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
                return LEGGINGS;

            case LEATHER_BOOTS:
            case IRON_BOOTS:
            case GOLD_BOOTS:
            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
                return BOOTS;

            default:
                throw new IllegalArgumentException(format("{0} isn't armor", material));
        }
    }
}