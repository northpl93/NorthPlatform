package pl.north93.zgame.api.bukkit.packets.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_10_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_10_R1.PacketPlayInClientCommand.EnumClientCommand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WrapperPlayInClientCommand extends AbstractWrapper
{
    private static final MethodHandle get_field_cmd = unreflectGetter(PacketPlayInClientCommand.class, "a");
    private static final MethodHandle set_field_cmd = unreflectSetter(PacketPlayInClientCommand.class, "a");
    private final PacketPlayInClientCommand packet;

    public WrapperPlayInClientCommand(final PacketPlayInClientCommand packet)
    {
        this.packet = packet;
    }

    public EnumClientCommand getClientCommand()
    {
        try
        {
            return (EnumClientCommand) get_field_cmd.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setClientCommand(final EnumClientCommand clientCommand)
    {
        try
        {
            set_field_cmd.invokeExact(this.packet, clientCommand);
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
