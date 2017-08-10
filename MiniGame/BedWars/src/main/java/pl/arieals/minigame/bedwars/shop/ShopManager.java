package pl.arieals.minigame.bedwars.shop;

import static java.text.MessageFormat.format;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.zgame.api.bukkit.utils.nms.ItemStackHelper.ensureCraftItemStack;
import static pl.north93.zgame.api.bukkit.utils.nms.ItemStackHelper.getPersistentStorage;
import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.cfg.BwShopConfig;
import pl.arieals.minigame.bedwars.cfg.BwShopEntry;
import pl.arieals.minigame.bedwars.event.ItemBuyEvent;
import pl.arieals.minigame.bedwars.event.ItemPreBuyEvent;
import pl.arieals.minigame.bedwars.shop.gui.ShopBase;
import pl.arieals.minigame.bedwars.shop.specialentry.IShopSpecialEntry;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.utils.itemstack.ItemTransaction;
import pl.north93.zgame.api.bukkit.utils.xml.XmlItemStack;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.uri.UriHandler;

/**
 * Klasa zarzadzajaca sklepem bedwarsów.
 * Zbiera specjalne handlery dodawania itemow,
 * obsluguje sprawdzanie czy item jest permamentny,
 * wystawia API dla gui.
 */
public class ShopManager
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private BwShopConfig  config;
    @Inject
    private IGuiManager   guiManager;
    private Map<String, IShopSpecialEntry> specialEntryMap = new HashMap<>();

    @Bean
    private ShopManager()
    {
    }

    @Aggregator(IShopSpecialEntry.class)
    private void collectSpecialShopEntries(final IShopSpecialEntry entry)
    {
        this.specialEntryMap.put(entry.getClass().getSimpleName(), entry);
    }

    /**
     * Sprawdza czy podany przedmiot jest pernamentny.
     * Informacja ta jest zawarta w jego tagach NBT.
     * @param itemStack przedmiot do sprawdzenia.
     * @return true jesli jest pernamentny.
     */
    public boolean isItemPermanent(final ItemStack itemStack)
    {
        final NBTTagCompound itemNbt = getPersistentStorage(ensureCraftItemStack(itemStack), "bedWars", false);
        return itemNbt != null && itemNbt.getBoolean("permanent");
    }

    /**
     * Oznacza dany przedmiot jako pernamentny.
     * Moze zwrocic nowa instancje itemu.
     * Uzywane sa tagi NBT.
     * @param itemStack przedmiot do oznaczenia.
     * @return Jesli podany ItemStack to CraftItemStack, zostanie
     *         zwrocona ta sama instancja. W przeciwnym wypadku
     *         kopia.
     */
    public ItemStack markAsPermanent(final ItemStack itemStack)
    {
        final ItemStack craftItemStack = ensureCraftItemStack(itemStack);
        final NBTTagCompound itemNbt = getPersistentStorage(craftItemStack, "bedWars", true);
        itemNbt.setBoolean("permanent", true);
        itemNbt.setString("northpl93", "kto pozwolil ci sie tu patrzec?");

        return craftItemStack;
    }

    @UriHandler("/minigame/bedwars/shop/buy/:name/:playerId")
    public boolean restHandler(final String calledUri, final Map<String, String> parameters)
    {
        final String name = parameters.get("name");
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

        return this.buy(player, name);
    }

    public BwShopEntry getShopEntry(final String name)
    {
        final BwShopEntry entry = findInCollection(this.config.getShopEntries(), BwShopEntry::getInternalName, name);
        if (entry == null)
        {
            throw new IllegalArgumentException(format("Shop entry with specified name {0} doesn't exists", name));
        }
        return entry;
    }

    /**
     * Uruchamia proces zakupu przedmiotu, lacznie z sprawdzaniem zaplaty.
     * @param player gracz ktoremu kupic przedmiot
     * @param name nazwa wewnetrzna shop entry.
     * @return czy sie udalo kupic i dodac przedmioty do ekwipunku.
     */
    public boolean buy(final Player player, final String name)
    {
        final BwShopEntry entry = this.getShopEntry(name);
        final ItemStack price = entry.getPrice().createItemStack();
        final LocalArena arena = getArena(player);

        final ItemPreBuyEvent preBuyEvent = this.apiCore.callEvent(new ItemPreBuyEvent(arena, player, entry, price, false));
        if (! preBuyEvent.getBuyStatus().canBuy())
        {
            return false;
        }

        // tworzymy interesujace nas itemstacki
        Stream<ItemStack> itemStream = entry.getItems().stream().map(XmlItemStack::createItemStack);
        if (entry.isPersistent())
        {
            itemStream = itemStream.map(this::markAsPermanent);
        }
        final List<ItemStack> items = itemStream.collect(Collectors.toList());
        this.apiCore.callEvent(new ItemBuyEvent(arena, player, entry, items));

        // obslugujemy dodanie itemów, przez specjalnego handlera lub normalnie przez ItemTransaction
        final IShopSpecialEntry specialEntry = this.specialEntryMap.get(entry.getSpecialHandler());
        final boolean success;
        if (specialEntry == null)
        {
            success = ItemTransaction.addItems(player.getInventory(), items);
        }
        else
        {
            success = specialEntry.buy(player, items);
        }

        if (! success)
        {
            // cos cos sie popsulo i nie bylo mnie slychac
            return false;
        }

        final Gui currentGui = this.guiManager.getCurrentGui(player);
        if (currentGui instanceof ShopBase)
        {
            currentGui.markDirty();
        }
        // pobieramy oplate dopiero jak sie udalo
        player.getInventory().removeItem(price);
        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("specialEntryMap", this.specialEntryMap).toString();
    }
}
