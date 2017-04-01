package pl.north93.zgame.api.bukkit.packets.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.PacketPlayOutSetSlot;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WrapperPlayOutSetSlot extends AbstractWrapper
{
    private static final MethodHandle get_field_windowId  = unreflectGetter(PacketPlayOutSetSlot.class, "a");
    private static final MethodHandle set_field_windowId  = unreflectSetter(PacketPlayOutSetSlot.class, "a");
    private static final MethodHandle get_field_slot      = unreflectGetter(PacketPlayOutSetSlot.class, "b");
    private static final MethodHandle set_field_slot      = unreflectSetter(PacketPlayOutSetSlot.class, "b");
    private static final MethodHandle get_field_itemstack = unreflectGetter(PacketPlayOutSetSlot.class, "c");
    private static final MethodHandle set_field_itemstack = unreflectSetter(PacketPlayOutSetSlot.class, "c");
    private final PacketPlayOutSetSlot packet;

    public WrapperPlayOutSetSlot(final PacketPlayOutSetSlot packet)
    {
        this.packet = packet;
    }

    public int getWindowId()
    {
        try
        {
            return (int) get_field_windowId.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setWindowId(final int windowId)
    {
        try
        {
            set_field_windowId.invokeExact(this.packet, windowId);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public int getSlotId()
    {
        try
        {
            return (int) get_field_slot.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setSlotId(final int slotId)
    {
        try
        {
            set_field_slot.invokeExact(this.packet, slotId);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public ItemStack getItemStack()
    {
        try
        {
            return (ItemStack) get_field_itemstack.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setItemStack(final ItemStack itemStack)
    {
        try
        {
            set_field_itemstack.invokeExact(this.packet, itemStack);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("packet", this.packet).toString();
    }
}