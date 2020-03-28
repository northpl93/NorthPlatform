package pl.north93.northplatform.minigame.elytrarace.shop;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.north93.northplatform.api.bukkit.utils.nms.ItemStackHelper;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.domain.ItemsGroup;

import java.util.Map;
import java.util.UUID;

public class HeadsManagement implements Listener
{
    @Inject
    private IGlobalShops globalShops;

    @Bean
    private HeadsManagement()
    {
    }

    public ItemStack getHeadOfPlayer(final Player player)
    {
        final Item activeItem = this.getActiveItem(player);
        if (activeItem == null)
        {
            return new ItemStack(Material.AIR);
        }
        return this.shopItemToItemStack(activeItem);
    }

    private ItemStack shopItemToItemStack(final Item item)
    {
        final Map<String, String> data = item.getData();

        final Material material = Material.getMaterial(data.get("material"));
        final byte itemData = Byte.parseByte(data.getOrDefault("data", "0"));

        final ItemStack itemStack = new ItemStack(material, 1, itemData);
        if (material == Material.SKULL_ITEM && itemData == 3)
        {
            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "NorthPL93");
            gameProfile.getProperties().put("textures", new Property("textures", data.get("textures"), data.get("sign")));

            final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            ItemStackHelper.applyProfileToHead(skullMeta, gameProfile);
            itemStack.setItemMeta(skullMeta);
        }

        return itemStack;
    }

    private Item getActiveItem(final Player player)
    {
        final IPlayerContainer container = this.globalShops.getPlayer(player);

        final ItemsGroup elytraHats = this.globalShops.getGroup("elytra_hats");
        final ItemsGroup elytraHeads = this.globalShops.getGroup("elytra_heads");

        Item activeItem;
        if ((activeItem = container.getActiveItem(elytraHats)) == null)
        {
            activeItem = container.getActiveItem(elytraHeads);
        }
        return activeItem;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
