package pl.north93.northplatform.minigame.bedwars.shop;

import static java.text.MessageFormat.format;

import static pl.north93.northplatform.api.bukkit.utils.nms.ItemStackHelper.ensureCraftItemStack;
import static pl.north93.northplatform.api.bukkit.utils.nms.ItemStackHelper.getOrCreatePersistentStorage;
import static pl.north93.northplatform.api.bukkit.utils.nms.ItemStackHelper.getPersistentStorage;
import static pl.north93.northplatform.api.global.utils.lang.CollectionUtils.findInCollection;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.bukkit.gui.IGuiManager;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ItemTransaction;
import pl.north93.northplatform.api.bukkit.utils.xml.itemstack.XmlItemStack;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.cfg.BwShopConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwShopEntry;
import pl.north93.northplatform.minigame.bedwars.event.ItemBuyEvent;
import pl.north93.northplatform.minigame.bedwars.event.ItemPreBuyEvent;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopBase;
import pl.north93.northplatform.minigame.bedwars.shop.specialentry.IShopSpecialEntry;

/**
 * Klasa zarzadzajaca sklepem bedwarsów.
 * Zbiera specjalne handlery dodawania itemow,
 * obsluguje sprawdzanie czy item jest permamentny,
 * wystawia API dla gui.
 */
public class ShopManager
{
    @Inject
    private BwShopConfig config;
    @Inject
    private IGuiManager guiManager;
    @Inject
    private IBukkitServerManager serverManager;
    private final Map<String, IShopSpecialEntry> specialEntryMap = new HashMap<>();

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
        final NBTTagCompound itemNbt = getPersistentStorage(ensureCraftItemStack(itemStack), "bedWars");
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
        final NBTTagCompound itemNbt = getOrCreatePersistentStorage(craftItemStack, "bedWars");
        itemNbt.setBoolean("permanent", true);
        itemNbt.setString("northpl93", "kto pozwolil ci sie tu patrzec?");

        return craftItemStack;
    }

    @UriHandler("/minigame/bedwars/shop/buy/:name/:playerId")
    public boolean restHandler(final UriInvocationContext context)
    {
        final String name = context.asString("name");
        final INorthPlayer player = INorthPlayer.get(context.asUuid("playerId"));

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
    public boolean buy(final INorthPlayer player, final String name)
    {
        final BwShopEntry entry = this.getShopEntry(name);
        final ItemStack price = entry.getPrice().createItemStack();
        final LocalArena arena = getArena(player);

        final ItemPreBuyEvent preBuyEvent = this.serverManager.callEvent(new ItemPreBuyEvent(arena, player, entry, price, false));
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
        this.serverManager.callEvent(new ItemBuyEvent(arena, player, entry, items));

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
