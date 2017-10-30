package pl.north93.zgame.api.bukkit.packets.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata;

public class WrapperPlayOutEntityMetadata extends AbstractWrapper<PacketPlayOutEntityMetadata>
{
    private static final MethodHandle get_field_entityId = unreflectGetter(PacketPlayOutEntityMetadata.class, "a");
    private static final MethodHandle set_field_entityId = unreflectSetter(PacketPlayOutEntityMetadata.class, "a");

    public WrapperPlayOutEntityMetadata(final PacketPlayOutEntityMetadata packet)
    {
        super(packet);
    }

    public int getEntityId()
    {
        try
        {
            return (int) get_field_entityId.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setEntityId(final int entityId)
    {
        try
        {
            set_field_entityId.invokeExact(this.packet, entityId);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }
}
