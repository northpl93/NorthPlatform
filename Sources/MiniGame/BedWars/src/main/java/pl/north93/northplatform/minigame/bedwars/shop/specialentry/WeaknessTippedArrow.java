package pl.north93.northplatform.minigame.bedwars.shop.specialentry;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.bedwars.shop.ShopGuiManager;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ItemTransaction;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;
import pl.north93.northplatform.api.global.utils.Vars;

public class WeaknessTippedArrow implements IShopSpecialEntry
{
    private final ShopGuiManager guiManager;
    private final MessagesBox shopMessages;

    private WeaknessTippedArrow(final ShopGuiManager guiManager, final @Messages("BedWarsShop") MessagesBox shopMessages)
    {
        this.guiManager = guiManager;
        this.shopMessages = shopMessages;
    }

    private ItemStack createBase()
    {
        final ItemStack itemStack = new ItemStack(Material.TIPPED_ARROW, 2);
        final PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 11, 1), true);
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }

    @UriHandler("/minigame/bedwars/shop/render/WeaknessTippedArrow/:name/:playerId")
    public ItemStack restRenderer(final UriInvocationContext context)
    {
        final Locale locale = INorthPlayer.get(context.asUuid("playerId")).getMyLocale();

        final String nameColor = this.guiManager.getNameColor(context);
        final String lore = this.guiManager.getLore(context);

        final ItemStack itemStack = this.createBase();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);

        final TranslatableString translatableDisplayName = TranslatableString.of(this.shopMessages, "@item.extras.exhaust_arrow.name$nameColor");
        itemMeta.setDisplayName(translatableDisplayName.getLegacy(locale, Vars.of("nameColor", nameColor)).asString());

        itemMeta.setLore(TranslatableString.of(this.shopMessages, "@gui.lore_placeholder$lore").getLegacy(locale, Vars.of("lore", lore)).asList());

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public boolean buy(final INorthPlayer player, final Collection<ItemStack> items)
    {
        final ItemStack itemStack = this.createBase();

        return ItemTransaction.addItems(player.getInventory(), Collections.singletonList(itemStack));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
