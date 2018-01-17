package pl.north93.zgame.api.bukkit.packets.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;

public class WrapperPlayOutEntityDestroy extends AbstractWrapper<PacketPlayOutEntityDestroy>
{
    private static final MethodHandle get_field_entities = unreflectGetter(PacketPlayOutEntityDestroy.class, "a");
    private static final MethodHandle set_field_entities = unreflectSetter(PacketPlayOutEntityDestroy.class, "a");

    public WrapperPlayOutEntityDestroy(final PacketPlayOutEntityDestroy packet)
    {
        super(packet);
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
}
