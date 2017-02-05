package pl.north93.zgame.skyblock.shop.cfg;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgExclude;

import pl.north93.zgame.skyblock.shop.api.ISpawnerEntry;

public class SpawnerEntryConfig implements ISpawnerEntry
{
    private String displayName;
    private String headTexture;
    private Double price;
    private String mobEntityType;

    @CfgExclude
    private ItemStack representingItem = null;
    @CfgExclude
    private EntityType mobType = null;

    @Override
    public String getDisplayName() { return this.displayName; }

    @Override
    public ItemStack getRepresentingItem() {
        if(this.representingItem != null)
        {
            return this.representingItem;
        }

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        ///Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19"}]}

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        //byte[] encodedData = Base64.encodeBase64( ("{textures:[{Value:\"" + this.headTexture + "\"}]}").getBytes() );
        profile.getProperties().put("textures", new Property("textures", this.headTexture));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        }
        catch (IllegalAccessException | NoSuchFieldException e)
        {
            e.printStackTrace();
        }

        skullMeta.setLore(Arrays.asList("Cena: " + this.price));
        skull.setItemMeta(skullMeta);
        this.representingItem = skull;
        return skull;
    }

    @Override
    public Double getPrice() { return this.price; }

    @Override
    public EntityType getMobEntityType()
    {
        if(this.mobType != null)
        {
            return mobType;
        }
        this.mobType = EntityType.valueOf(this.mobEntityType);
        return this.mobType;
    }


    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("displayName", this.displayName).append("price", this.price).toString();
    }
}
