package pl.north93.zgame.api.bukkit.utils.nms;

import static org.diorite.commons.reflections.DioriteReflectionUtils.getField;


import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import lombok.NonNull;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import org.diorite.commons.reflections.FieldAccessor;

public final class ItemStackHelper
{
    private static final FieldAccessor<GameProfile> craftMetaSkull_profile = getField("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaSkull", "profile", GameProfile.class);
    private static final FieldAccessor<ItemStack>   craftItemStack_handle  = getField(CraftItemStack.class, "handle", ItemStack.class);

    private ItemStackHelper()
    {
    }

    /**
     * Upewnia sie ze podany Bukkitowy ItemStack to CraftItemStack.
     * Jesli nie zwroci kopie.
     *
     * @param itemStack ItemStack do sprawdzenia i ew. skopiowania.
     * @return CraftItemStack.
     */
    public static org.bukkit.inventory.ItemStack ensureCraftItemStack(final org.bukkit.inventory.ItemStack itemStack)
    {
        if (itemStack instanceof CraftItemStack)
        {
            return itemStack;
        }
        return CraftItemStack.asCraftCopy(itemStack);
    }

    /**
     * Zwraca NMSowy ItemStack z podanego CraftItemStacka.
     *
     * @param bukkitStack CraftItemStack.
     * @throws IllegalArgumentException Gdy podana zostanie inna implementacja ItemStacka niz CraftItemStack.
     * @return NMSowy ItemStack. (instancja z pola handle z CraftItemStack.)
     */
    public static ItemStack getHandle(final org.bukkit.inventory.ItemStack bukkitStack)
    {
        if (! (bukkitStack instanceof CraftItemStack))
        {
            throw new IllegalArgumentException("NMS ItemStack can be only obtained from CraftItemStack. Use ensureCraftItemStack or CraftItemStack#asCraftCopy");
        }
        return craftItemStack_handle.get(bukkitStack);
    }

    public static NBTTagCompound getPersistentStorage(@NonNull org.bukkit.inventory.ItemStack bukkitStack, @NonNull String storageName)
    {
        return getHandle(bukkitStack).d(storageName); // ItemStakc#d() should be getPersistentStorageIfExists()
    }
    
    public static NBTTagCompound getOrCreatePersistentStorage(@NonNull org.bukkit.inventory.ItemStack bukkitStack,  @NonNull String storageName)
    {
        return getHandle(bukkitStack).c(storageName); // ItemStack#c() should be getOrCreatePersistentStorage()
    }

    /**
     * Ustawia zmienna profile w danym SkullMeta.
     *
     * @param skullMeta SkullMeta w ktorym zmieniamy gameprofile.
     * @param gameProfile Nowe GameProfile.
     */
    public static void applyProfileToHead(final SkullMeta skullMeta, final GameProfile gameProfile)
    {
        craftMetaSkull_profile.set(skullMeta, gameProfile);
    }
}
