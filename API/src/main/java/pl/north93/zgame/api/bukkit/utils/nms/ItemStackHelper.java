package pl.north93.zgame.api.bukkit.utils.nms;

import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;

import org.diorite.utils.reflections.DioriteReflectionUtils;
import org.diorite.utils.reflections.FieldAccessor;

public final class ItemStackHelper
{
    private static final FieldAccessor<ItemStack> craftItemStack_handle = DioriteReflectionUtils.getField(CraftItemStack.class, "handle", ItemStack.class);

    private ItemStackHelper()
    {
    }

    public static org.bukkit.inventory.ItemStack ensureCraftItemStack(final org.bukkit.inventory.ItemStack itemStack)
    {
        if (itemStack instanceof CraftItemStack)
        {
            return itemStack;
        }
        return CraftItemStack.asCraftCopy(itemStack);
    }

    public static ItemStack getHandle(final org.bukkit.inventory.ItemStack bukkitStack)
    {
        if (! (bukkitStack instanceof CraftItemStack))
        {
            throw new IllegalArgumentException("NMS ItemStack can be only obtained from CraftItemStack. Use ensureCraftItemStack or CraftItemStack#asCraftCopy");
        }
        return craftItemStack_handle.get(bukkitStack);
    }

    public static NBTTagCompound getPersistentStorage(final org.bukkit.inventory.ItemStack bukkitStack, final String storageName, final boolean create)
    {
        final ItemStack handle = getHandle(bukkitStack);
        return handle.a(storageName, create); // if(this.tag != null && this.tag.hasKeyOfType(s, 10)) {
    }
}
