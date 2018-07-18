package pl.north93.zgame.api.bukkit.protocol.wrappers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.Packet;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;

public abstract class AbstractWrapper<T extends Packet>
{
    protected static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    protected final T packet;

    public AbstractWrapper(final T packet)
    {
        this.packet = packet;
    }

    public final T getPacket()
    {
        return this.packet;
    }

    public final void sendTo(final Player player)
    {
        final EntityPlayer entityPlayer = INorthPlayer.asCraftPlayer(player).getHandle();
        entityPlayer.playerConnection.sendPacket(this.packet);
    }

    protected static MethodHandle unreflectGetter(final Class<?> clazz, final String name)
    {
        try
        {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return lookup.unreflectGetter(field);
        }
        catch (final IllegalAccessException | NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected static MethodHandle unreflectSetter(final Class<?> clazz, final String name)
    {
        try
        {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return lookup.unreflectSetter(field);
        }
        catch (final IllegalAccessException | NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("packet", this.packet).toString();
    }
}
