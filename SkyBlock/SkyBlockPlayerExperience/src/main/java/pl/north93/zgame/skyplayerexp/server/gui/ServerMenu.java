package pl.north93.zgame.skyplayerexp.server.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.windows.ClickHandler;
import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class ServerMenu extends Window
{
    private static final List<String> LORE_LOADING = lore("&7Trwa wczytywanie");
    private static final ItemStack    PLACEHOLDER;
    static
    {
        PLACEHOLDER = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        final ItemMeta itemMeta = PLACEHOLDER.getItemMeta();
        itemMeta.setDisplayName(" ");
        PLACEHOLDER.setItemMeta(itemMeta);
    }

    // object data
    private final ServerGuiManager serverGuiManager;
    private final Player           player;
    private ItemStack tpToIsland;
    private ItemStack playerHead;
    private ItemStack myIsland;
    private ItemStack vipAddons;
    private ItemStack myPets;
    private ItemStack quests;

    public ServerMenu(final ServerGuiManager serverGuiManager, final Player player)
    {
        super("MENU SERWERA", 5 * 9);
        this.serverGuiManager = serverGuiManager;
        this.player = player;
    }

    @Override
    protected void onShow()
    {
        {
            this.tpToIsland = new ItemStack(Material.GRASS, 1);
            final ItemMeta itemMeta = this.tpToIsland.getItemMeta();
            itemMeta.setDisplayName(color("&6Przenies na wyspe"));
            itemMeta.setLore(LORE_LOADING);
            this.tpToIsland.setItemMeta(itemMeta);
            this.addElement(10, this.tpToIsland);
        }

        {
            this.playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            final SkullMeta skull = (SkullMeta) this.playerHead.getItemMeta();
            skull.setOwner(this.player.getName());
            skull.setDisplayName(color("&6" + this.player.getName()));
            skull.setLore(LORE_LOADING);
            this.playerHead.setItemMeta(skull);
            this.addElement(12, this.playerHead);
        }

        {
            final ItemStack shop = new ItemStack(Material.DOUBLE_PLANT, 1);
            final ItemMeta itemMeta = shop.getItemMeta();
            itemMeta.setDisplayName(color("&6Sklep"));
            shop.setItemMeta(itemMeta);
            this.addElement(14, shop, event -> this.serverGuiManager.openShopCategories(this.player));
        }

        {
            final ItemStack vipShop = new ItemStack(Material.GOLD_INGOT, 1);
            final ItemMeta itemMeta = vipShop.getItemMeta();
            itemMeta.setDisplayName(color("&6Sklep VIP"));
            vipShop.setItemMeta(itemMeta);
            this.addElement(16, vipShop, event -> this.serverGuiManager.openVipShop(this.player));
        }

        {
            this.myIsland = new ItemStack(Material.BOOK_AND_QUILL);
            final ItemMeta itemMeta = this.myIsland.getItemMeta();
            itemMeta.setDisplayName(color("&6Twoja wyspa"));
            itemMeta.setLore(LORE_LOADING);
            this.myIsland.setItemMeta(itemMeta);
            this.addElement(28, this.myIsland);
        }

        {
            this.vipAddons = new ItemStack(Material.NETHER_STAR, 1);
            final ItemMeta itemMeta = this.vipAddons.getItemMeta();
            itemMeta.setDisplayName(color("&6Dodatki dla VIPa"));
            itemMeta.setLore(lore("&7WKRÓTCE!"));
            this.vipAddons.setItemMeta(itemMeta);
            this.addElement(30, this.vipAddons);
        }

        {
            this.myPets = new ItemStack(Material.NAME_TAG, 1);
            final ItemMeta itemMeta = this.myPets.getItemMeta();
            itemMeta.setDisplayName(color("&6Twój zwierzak"));
            itemMeta.setLore(lore("&7WKRÓTCE!"));
            this.myPets.setItemMeta(itemMeta);
            this.addElement(32, this.myPets);
        }

        {
            this.quests = new ItemStack(Material.BOOK, 1);
            final ItemMeta itemMeta = this.quests.getItemMeta();
            itemMeta.setDisplayName(color("&6Zadania"));
            itemMeta.setLore(lore("&7WKRÓTCE!"));
            this.quests.setItemMeta(itemMeta);
            this.addElement(34, this.quests);
        }

        {
            this.fillEmpty(PLACEHOLDER);
        }
    }

    public void loadAsyncData(final Value<IOnlinePlayer> player)
    {
        final SkyPlayer skyPlayer = SkyPlayer.get(player);
        final boolean hasIsland = skyPlayer.hasIsland();

        {
            final ItemMeta itemMeta = this.tpToIsland.getItemMeta();
            final ClickHandler action;
            if (hasIsland)
            {
                itemMeta.setLore(lore("&7Kliknij, aby przeniesc", "&7sie na wyspe"));
                action = ev -> this.player.performCommand("home");
            }
            else
            {
                itemMeta.setDisplayName(color("&6Stworz wyspe"));
                itemMeta.setLore(lore("&7Kliknij, aby stworzyc", "&7nowa wyspe"));
                action = ev -> this.player.performCommand("create");
            }
            this.tpToIsland.setItemMeta(itemMeta);
            this.addElement(10, this.tpToIsland, action);
        }

        {
            final ItemMeta itemMeta = this.playerHead.getItemMeta();
            itemMeta.setLore(lore("&7Kliknij, aby otworzyc", "&7ustawienia serwera"));
            this.playerHead.setItemMeta(itemMeta);
            this.addElement(12, this.playerHead, ev -> this.serverGuiManager.openServerOptions(this.player));
        }

        {
            final ItemMeta itemMeta = this.myIsland.getItemMeta();
            final ClickHandler action;
            if (hasIsland)
            {
                itemMeta.setLore(lore("&7Kliknij, aby otworzyc", "&7ustawienia wyspy"));
                action = ev -> this.serverGuiManager.openIslandOptions(this.player);
            }
            else
            {
                itemMeta.setLore(lore("&cAby uzyc tej opcji", "&cmusisz miec wyspe"));
                action = ev -> {};
            }
            this.myIsland.setItemMeta(itemMeta);
            this.addElement(28, this.myIsland, action);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverGuiManager", this.serverGuiManager).toString();
    }
}
