package pl.north93.zgame.api.bukkit.packets.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WrapperPlayOutEntityDestroy extends AbstractWrapper
{
    private static final MethodHandle get_field_entities = unreflectGetter(PacketPlayOutEntityDestroy.class, "a");
    private static final MethodHandle set_field_entities = unreflectSetter(PacketPlayOutEntityDestroy.class, "a");
    private final PacketPlayOutEntityDestroy packet;

    public WrapperPlayOutEntityDestroy(final PacketPlayOutEntityDestroy packet)
    {
        this.packet = packet;
    }

    public int[] getEntities()
    {
        try
        {
            return (int[]) get_field_entities.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setEntities(final byte[] entities)
    {
        try
        {
            set_field_entities.invokeExact(this.packet, entities);
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
