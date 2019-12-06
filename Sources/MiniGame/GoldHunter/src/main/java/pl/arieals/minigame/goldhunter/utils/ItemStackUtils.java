package pl.arieals.minigame.goldhunter.utils;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.global.utils.lang.MethodHandlesUtils;
import pl.north93.northplatform.api.global.utils.lang.SneakyThrow;

public final class ItemStackUtils
{
    private static final MethodHandle HANDLE_GETTER = MethodHandlesUtils.unreflectGetter(CraftItemStack.class, "handle");
    
    private ItemStackUtils()
    {
    }
    
    public static ItemStack prepareItem(GoldHunterPlayer player, ItemStack is)
    {
        tryPrepareAssasinDagger(player, is);
        hideAttributesAndMakeUnbreakable(is);
        return is;
    }
    
    
    public static ItemStack hideAttributesAndMakeUnbreakable(ItemStack is)
    {
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        is.setItemMeta(itemMeta);
        return is;
    }
    
    public static boolean isAssasinDagger(ItemStack is)
    {
        NBTTagCompound ghmetadata = getGoldHunterMetadata(is);
        return ghmetadata != null && ghmetadata.hasKey("assasinDagger");
    }
    
    private static void tryPrepareAssasinDagger(GoldHunterPlayer player, ItemStack is)
    {
        if ( !isAssasinDagger(is) )
        {
            return;
        }
        
        // TODO: add way to configure this!
        
        net.minecraft.server.v1_12_R1.ItemStack nms = (net.minecraft.server.v1_12_R1.ItemStack) SneakyThrow.sneaky(() -> HANDLE_GETTER.invoke(is));
        NBTTagCompound ghmetadata = nms.d("goldhunter");
        ghmetadata.setString("message", "There aren't any easter eggs here! Go away!");
        
        NBTTagCompound modifier = new NBTTagCompound();
        modifier.setString("AttributeName", "generic.attackDamage");
        modifier.setString("Name", "BaseDamage");
        modifier.setString("Slot", "mainhand");
        modifier.setInt("Operation", 0);
        modifier.setDouble("Amount", 4.5); // the attack value
        
        UUID uuid = UUID.randomUUID();
        modifier.setLong("UUIDMost", uuid.getMostSignificantBits());
        modifier.setLong("UUIDLeast", uuid.getLeastSignificantBits());
        
        NBTTagList modifiers = new NBTTagList();
        modifiers.add(modifier);
        
        nms.getTag().set("AttributeModifiers", modifiers);
        
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(player.getMessage("dagger.name"));
        meta.setLore(Arrays.asList(player.getMessageLines("dagger.lore")));
        meta.addEnchant(Enchantment.OXYGEN, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        is.setItemMeta(meta);
    }
    
    public static NBTTagCompound getGoldHunterMetadata(ItemStack is)
    {
        if ( !( is instanceof CraftItemStack ) )
        {
            return null;
        }
        
        net.minecraft.server.v1_12_R1.ItemStack nms = (net.minecraft.server.v1_12_R1.ItemStack) SneakyThrow.sneaky(() -> HANDLE_GETTER.invoke(is));
        if ( nms == null )
        {
            return null;
        }
        
        return nms.d("goldhunter");
    }
    
    public static NBTTagCompound getGoldHunterMetadataSection(ItemStack is, String key)
    {
        NBTTagCompound metadata = getGoldHunterMetadata(is);
        if ( metadata == null )
        {
            return null;
        }
        
        return metadata.hasKeyOfType(key, 10) ? metadata.getCompound(key) : null;
    }
}
